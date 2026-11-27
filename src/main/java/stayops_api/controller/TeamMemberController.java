package stayops_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import stayops_api.dto.TeamMemberDto;
import stayops_api.entity.TeamMember;
import stayops_api.entity.User;
import stayops_api.repository.TeamMemberRepository;
import stayops_api.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "12. Team & Staff", description = "Team member management, roles, and staff login authentication")
@RestController
@RequestMapping("/api/team")
public class TeamMemberController {

    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TeamMemberController(
            TeamMemberRepository teamMemberRepository,
            UserRepository userRepository
    ) {
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Get team members for current owner (seeds demo staff if empty)")
    @GetMapping
    public ResponseEntity<List<TeamMemberDto>> getTeamMembers(Authentication authentication) {
        Long ownerId = getOwnerId(authentication);
        List<TeamMember> members = teamMemberRepository.findByOwnerId(ownerId);

        // Auto-seed or adopt default PRD demo staff if owner has no team members yet
        if (members.isEmpty()) {
            members = seedDefaultTeam(ownerId);
        } else {
            boolean hasCaretaker = members.stream().anyMatch(m -> m.getRole() != null && m.getRole().toLowerCase().contains("caretaker"));
            if (!hasCaretaker) {
                teamMemberRepository.findByMobileNumber("9876543210").ifPresent(r -> {
                    r.setOwnerId(ownerId);
                    teamMemberRepository.save(r);
                });
                members = teamMemberRepository.findByOwnerId(ownerId);
            }
        }

        List<TeamMemberDto> dtos = members.stream()
                .map(m -> TeamMemberDto.fromEntity(m, objectMapper))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Create a new team member")
    @PostMapping
    public ResponseEntity<?> createTeamMember(
            @RequestBody TeamMemberDto payload,
            Authentication authentication
    ) {
        if (payload.getName() == null || payload.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Staff member name is required"));
        }

        String mobile = payload.getMobileNumber() != null ? payload.getMobileNumber().trim() : "";
        if (mobile.isEmpty() || !mobile.matches("^\\d{10}$")) {
            return ResponseEntity.badRequest().body(Map.of("message", "A valid 10-digit mobile number is required"));
        }

        if (teamMemberRepository.existsByMobileNumber(mobile)) {
            return ResponseEntity.badRequest().body(Map.of("message", "A team member with this mobile number already exists"));
        }

        String pin = payload.getPin() != null && !payload.getPin().trim().isEmpty() 
                ? payload.getPin().trim() 
                : (payload.getPassword() != null ? payload.getPassword().trim() : "");
        if (pin.length() < 4 || pin.length() > 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "Access PIN must be 4 to 6 digits"));
        }

        Long ownerId = getOwnerId(authentication);

        TeamMember member = new TeamMember();
        member.setOwnerId(ownerId);
        payload.updateEntity(member, objectMapper);
        member.setMobileNumber(mobile);
        member.setPin(pin);
        member.setPassword(pin);
        if (member.getStatus() == null || member.getStatus().isEmpty()) {
            member.setStatus("Active");
        }

        TeamMember saved = teamMemberRepository.save(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(TeamMemberDto.fromEntity(saved, objectMapper));
    }

    @Operation(summary = "Update an existing team member")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTeamMember(
            @PathVariable Long id,
            @RequestBody TeamMemberDto payload
    ) {
        Optional<TeamMember> opt = teamMemberRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Team member not found"));
        }

        TeamMember member = opt.get();

        if (payload.getMobileNumber() != null) {
            String mobile = payload.getMobileNumber().trim();
            if (!mobile.matches("^\\d{10}$")) {
                return ResponseEntity.badRequest().body(Map.of("message", "A valid 10-digit mobile number is required"));
            }
            if (teamMemberRepository.existsByMobileNumberAndIdNot(mobile, id)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Another team member with this mobile number already exists"));
            }
        }

        if (payload.getPin() != null && !payload.getPin().trim().isEmpty()) {
            String pin = payload.getPin().trim();
            if (pin.length() < 4 || pin.length() > 6) {
                return ResponseEntity.badRequest().body(Map.of("message", "Access PIN must be 4 to 6 digits"));
            }
        }

        payload.updateEntity(member, objectMapper);
        TeamMember updated = teamMemberRepository.save(member);

        return ResponseEntity.ok(TeamMemberDto.fromEntity(updated, objectMapper));
    }

    @Operation(summary = "Delete a team member")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTeamMember(@PathVariable Long id) {
        Optional<TeamMember> opt = teamMemberRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Team member not found"));
        }

        teamMemberRepository.delete(opt.get());
        return ResponseEntity.ok(Map.of("success", true, "message", "Team member removed successfully"));
    }

    @Operation(summary = "Authenticate staff login via mobile and PIN")
    @PostMapping("/login")
    public ResponseEntity<?> authenticateStaffLogin(@RequestBody Map<String, String> credentials) {
        String input = credentials.get("mobileNumber");
        if (input == null || input.trim().isEmpty()) {
            input = credentials.get("email");
        }
        String pin = credentials.get("pin");
        if (pin == null || pin.trim().isEmpty()) {
            pin = credentials.get("password");
        }

        if (input == null || input.trim().isEmpty() || pin == null || pin.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mobile number and PIN are required"));
        }

        String cleanMobile = input.trim();
        String cleanPin = pin.trim();

        Optional<TeamMember> opt = teamMemberRepository.findByMobileNumber(cleanMobile);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid mobile number or PIN"));
        }

        TeamMember staff = opt.get();
        boolean pinMatches = cleanPin.equals(staff.getPin()) || cleanPin.equals(staff.getPassword());
        if (!pinMatches) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid mobile number or PIN"));
        }

        if (!"Active".equalsIgnoreCase(staff.getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Account is inactive. Please contact the property owner."));
        }

        return ResponseEntity.ok(TeamMemberDto.fromEntity(staff, objectMapper));
    }

    private Long getOwnerId(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && authentication.getName() != null) {
            try {
                return Long.valueOf(authentication.getName());
            } catch (NumberFormatException ignored) {
            }
        }
        return userRepository.findAll().stream().findFirst().map(User::getId).orElse(1L);
    }

    private List<TeamMember> seedDefaultTeam(Long ownerId) {
        List<TeamMember> seeded = new ArrayList<>();

        // 1. Ramesh Kumar (Caretaker)
        TeamMember ramesh = teamMemberRepository.findByMobileNumber("9876543210").orElse(null);
        if (ramesh == null) {
            ramesh = new TeamMember();
            ramesh.setMobileNumber("9876543210");
        }
        ramesh.setOwnerId(ownerId);
        ramesh.setName("Ramesh Kumar");
        ramesh.setPin("1234");
        ramesh.setPassword("1234");
        ramesh.setRole("Caretaker / Ground Staff");
        ramesh.setAssignedProperties("[\"1\"]");
        ramesh.setAssignedPropertyNames("[\"Sri Nature Villa\"]");
        ramesh.setPermissions("{\"photoEvidence\":true,\"pettyCash\":true,\"mealLogging\":true,\"manageBookings\":false}");
        ramesh.setStatus("Active");
        seeded.add(teamMemberRepository.save(ramesh));

        // 2. Vikram Singh (Property Manager)
        TeamMember vikram = teamMemberRepository.findByMobileNumber("9123456789").orElse(null);
        if (vikram == null) {
            vikram = new TeamMember();
            vikram.setMobileNumber("9123456789");
        }
        vikram.setOwnerId(ownerId);
        vikram.setName("Vikram Singh");
        vikram.setPin("5678");
        vikram.setPassword("5678");
        vikram.setRole("Property Manager");
        vikram.setAssignedProperties("[\"1\",\"2\"]");
        vikram.setAssignedPropertyNames("[\"Sri Nature Villa\",\"Coorg Heights Homestay\"]");
        vikram.setPermissions("{\"photoEvidence\":true,\"pettyCash\":true,\"mealLogging\":true,\"manageBookings\":true}");
        vikram.setStatus("Active");
        seeded.add(teamMemberRepository.save(vikram));

        return seeded;
    }
}
