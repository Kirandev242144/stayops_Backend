package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import stayops_api.entity.User;
import stayops_api.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

import stayops_api.dto.LoginRequest;
import stayops_api.dto.LoginResponse;
import stayops_api.dto.SignupRequest;
import stayops_api.dto.SignupResponse;
import stayops_api.service.AuthService;

@Tag(name = "1. Authentication", description = "User Signup & Login API")
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @Autowired
    private UserRepository userRepository;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // =========================
    // SIGNUP
    // =========================

    @Operation(summary = "Register a new Property Owner account")
    @PostMapping("/signup")
    public SignupResponse signup(
            @RequestBody SignupRequest request
    ) {
        return authService.signup(request);
    }

    // =========================
    // LOGIN
    // =========================

    @Operation(summary = "Log in to receive a JWT Authorization Token")
    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }

    // =========================
    // CURRENT USER PROFILE FROM DATABASE
    // =========================

    @Operation(summary = "Get current authenticated property owner profile from database")
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            Long userId = Long.valueOf(authentication.getName());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> res = new HashMap<>();
            res.put("id", user.getId());
            res.put("name", user.getName());
            res.put("email", user.getEmail());

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
}