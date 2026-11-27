package stayops_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import stayops_api.dto.LoginRequest;
import stayops_api.dto.LoginResponse;
import stayops_api.dto.SignupRequest;
import stayops_api.dto.SignupResponse;
import stayops_api.entity.User;
import stayops_api.repository.UserRepository;
import stayops_api.repository.PropertyRepository;
import stayops_api.security.JwtService;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PropertyRepository propertyRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // =========================================================
    // SIGNUP
    // =========================================================

    public SignupResponse signup(SignupRequest request) {

        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }

        // Check whether email already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("An account with this email address already exists. Please log in.");
        }

        User user = new User();
        user.setName(request.getName() != null ? request.getName().trim() : "Property Host");
        user.setEmail(email);
        user.setPhone(request.getPhone());
        user.setRole("Owner");
        user.setStatus("Active");

        String planKey = request.getPlan() != null ? request.getPlan().toLowerCase().trim() : "pro";
        user.setPlan(planKey);
        user.setTrialDaysLeft(14);

        // NEVER store plain-text passwords
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        User savedUser = userRepository.save(user);

        // Provision initial property if specified in onboarding
        Long propertyId = null;
        String propertyName = null;
        if (request.getPropertyName() != null && !request.getPropertyName().trim().isBlank()) {
            stayops_api.entity.Property prop = new stayops_api.entity.Property();
            prop.setUser(savedUser);
            propertyName = request.getPropertyName().trim();
            prop.setName(propertyName);
            prop.setType(request.getPropertyType() != null && !request.getPropertyType().isBlank() ? request.getPropertyType() : "Homestay");
            prop.setRooms(request.getRoomsCount() != null && request.getRoomsCount() > 0 ? request.getRoomsCount() : 3);
            prop.setCity(request.getCity() != null && !request.getCity().isBlank() ? request.getCity() : "India");
            prop.setStatus("Active");
            stayops_api.entity.Property savedProp = propertyRepository.save(prop);
            propertyId = savedProp.getId();
        }

        // Generate immediate JWT token for frictionless onboarding
        String token = jwtService.generateToken(savedUser.getId(), savedUser.getEmail());

        SignupResponse response = new SignupResponse();
        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());
        response.setToken(token);
        response.setPlan(savedUser.getPlan());
        response.setTrialDaysLeft(savedUser.getTrialDaysLeft());
        response.setPropertyId(propertyId);
        response.setPropertyName(propertyName);

        return response;
    }


    // =========================================================
    // LOGIN
    // =========================================================

    public LoginResponse login(LoginRequest request) {

        // Find user by email
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Invalid email or password"
                        )
                );

        // Compare entered password with BCrypt password
        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }

        // Verify tenant property ownership if logging in from a property subdomain
        if (request.getSubdomain() != null && !request.getSubdomain().isBlank()) {
            String rawSub = request.getSubdomain().trim().toLowerCase();
            String cleanSub = rawSub.replaceAll("[^a-z0-9]", "");
            if (!cleanSub.equals("localhost") && !cleanSub.equals("127001") && !cleanSub.equals("app") && !cleanSub.equals("www")) {
                List<stayops_api.entity.Property> userProperties = propertyRepository.findByUserId(user.getId());
                boolean ownsTargetProperty = false;
                for (stayops_api.entity.Property p : userProperties) {
                    if (String.valueOf(p.getId()).equals(rawSub)) {
                        ownsTargetProperty = true;
                        break;
                    }
                    if (p.getName() != null) {
                        String cleanPName = p.getName().trim().toLowerCase().replaceAll("[^a-z0-9]", "");
                        if (cleanPName.equals(cleanSub) || cleanPName.contains(cleanSub) || cleanSub.contains(cleanPName)) {
                            ownsTargetProperty = true;
                            break;
                        }
                    }
                    if (p.getOperations() != null && p.getOperations().toLowerCase().contains(cleanSub)) {
                        ownsTargetProperty = true;
                        break;
                    }
                }

                if (!ownsTargetProperty) {
                    throw new IllegalArgumentException(
                            "Unauthorized: Account (" + request.getEmail() + ") does not own or have management access for the '" + request.getSubdomain() + "' property."
                    );
                }
            }
        }

        // Generate JWT using our JwtService
        String token =
                jwtService.generateToken(
                        user.getId(),
                        user.getEmail()
                );

        // Create response
        LoginResponse response = new LoginResponse();

        response.setToken(token);
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());

        return response;
    }
}