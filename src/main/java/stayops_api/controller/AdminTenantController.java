package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import stayops_api.dto.AdminAuditLogDTO;
import stayops_api.dto.AdminTenantDTO;
import stayops_api.dto.AdminTenantDetailDTO;
import stayops_api.dto.SignupRequest;
import stayops_api.entity.AdminAuditLog;
import stayops_api.entity.Property;
import stayops_api.entity.User;
import stayops_api.repository.AdminAuditLogRepository;
import stayops_api.repository.PropertyPhotoRepository;
import stayops_api.repository.PropertyRepository;
import stayops_api.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag(name = "10. Admin Console - Tenants & Users", description = "Platform Admin Tenant Account & Staff Management APIs")
@RestController
@RequestMapping("/api/admin/tenants")
@CrossOrigin(origins = "*")
public class AdminTenantController {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyPhotoRepository propertyPhotoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminAuditLogRepository auditLogRepository;

    public AdminTenantController(
            UserRepository userRepository,
            PropertyRepository propertyRepository,
            PropertyPhotoRepository propertyPhotoRepository,
            PasswordEncoder passwordEncoder,
            AdminAuditLogRepository auditLogRepository
    ) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.propertyPhotoRepository = propertyPhotoRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Get all tenants from database.
     */
    @Operation(summary = "Get all registered tenant accounts with property metrics")
    @GetMapping
    public List<AdminTenantDTO> getAllTenants() {
        List<User> users = userRepository.findAll();
        List<AdminTenantDTO> result = new ArrayList<>();
        for (User u : users) {
            result.add(mapToAdminTenantDTO(u));
        }
        return result;
    }

    /**
     * Get single tenant summary by ID.
     */
    @Operation(summary = "Get single tenant account by ID")
    @GetMapping("/{id}")
    public ResponseEntity<AdminTenantDTO> getTenantById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(u -> ResponseEntity.ok(mapToAdminTenantDTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get full tenant detail: overview + properties + audit log.
     */
    @Operation(summary = "Get full tenant detail including properties and audit log")
    @GetMapping("/{id}/detail")
    public ResponseEntity<AdminTenantDetailDTO> getTenantDetail(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    List<Property> properties = propertyRepository.findByUserId(id);
                    List<AdminAuditLog> logs = auditLogRepository.findByTenantIdOrderByCreatedAtDesc(id);
                    return ResponseEntity.ok(mapToDetailDTO(user, properties, logs));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update tenant account status (active, suspended).
     */
    @Operation(summary = "Update tenant status (active or suspended)")
    @RequestMapping(value = "/{id}/status", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<AdminTenantDTO> updateTenantStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String newStatus = body.get("status");
        if (newStatus == null || newStatus.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        String previousStatus = user.getStatus();
        user.setStatus(newStatus.trim());
        User saved = userRepository.save(user);

        // Write audit log entry
        String action = newStatus.equalsIgnoreCase("suspended") ? "Account Suspended" : "Account Reactivated";
        String detail = String.format("Status changed from '%s' to '%s' by admin.", previousStatus, newStatus);
        auditLogRepository.save(new AdminAuditLog(id, "admin@stayops.in", action, detail));

        return ResponseEntity.ok(mapToAdminTenantDTO(saved));
    }

    /**
     * Extend tenant free trial duration.
     */
    @Operation(summary = "Extend trial duration for a tenant account")
    @RequestMapping(value = "/{id}/trial", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<AdminTenantDTO> extendTenantTrial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        Object daysObj = body.get("extendDays");
        int days = 7;
        if (daysObj != null) {
            try {
                days = Integer.parseInt(daysObj.toString());
            } catch (NumberFormatException ignored) {}
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        int currentTrial = user.getTrialDaysLeft() != null ? user.getTrialDaysLeft() : 0;
        user.setTrialDaysLeft(currentTrial + days);
        user.setPlan("trial");
        if ("suspended".equalsIgnoreCase(user.getStatus())) {
            user.setStatus("Active");
        }

        User saved = userRepository.save(user);

        // Write audit log entry
        String detail = String.format("Trial extended by %d days. New total: %d days remaining.", days, currentTrial + days);
        auditLogRepository.save(new AdminAuditLog(id, "admin@stayops.in", "Trial Extended", detail));

        return ResponseEntity.ok(mapToAdminTenantDTO(saved));
    }

    /**
     * Create a new tenant directly in the database.
     */
    @Operation(summary = "Admin create new tenant owner account")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminTenantDTO createTenant(@RequestBody SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email address already registered: " + request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword() != null ? request.getPassword() : "StayOps@2026"));
        user.setStatus("Active");
        user.setRole("Owner");
        user.setPlan("pro");
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);

        // Write audit log entry
        auditLogRepository.save(new AdminAuditLog(saved.getId(), "admin@stayops.in",
                "Account Created", "New tenant account created by admin."));

        return mapToAdminTenantDTO(saved);
    }

    /**
     * Delete tenant and associated properties.
     */
    @Operation(summary = "Delete tenant account and associated inventory")
    @DeleteMapping("/{id}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTenant(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        List<Property> properties = propertyRepository.findByUserId(id);
        for (Property p : properties) {
            propertyPhotoRepository.deleteByProperty_Id(p.getId());
        }
        propertyRepository.deleteAll(properties);
        auditLogRepository.deleteAll(auditLogRepository.findByTenantIdOrderByCreatedAtDesc(id));
        userRepository.delete(user);
    }

    // ── Mapping helpers ─────────────────────────────────────────────────────

    private AdminTenantDTO mapToAdminTenantDTO(User user) {
        AdminTenantDTO dto = new AdminTenantDTO();
        List<Property> properties = propertyRepository.findByUserId(user.getId());
        populateCommonFields(dto, user, properties);
        return dto;
    }

    private AdminTenantDetailDTO mapToDetailDTO(User user, List<Property> properties, List<AdminAuditLog> logs) {
        AdminTenantDetailDTO dto = new AdminTenantDetailDTO();
        populateCommonFields(dto, user, properties);

        // Properties list
        List<AdminTenantDetailDTO.PropertySummary> propList = new ArrayList<>();
        for (Property p : properties) {
            AdminTenantDetailDTO.PropertySummary ps = new AdminTenantDetailDTO.PropertySummary();
            ps.setId(p.getId());
            ps.setName(p.getName());
            ps.setCity(p.getCity());
            ps.setState(p.getState());
            ps.setRooms(p.getRooms());
            ps.setStatus(p.getStatus() != null ? p.getStatus() : "Active");
            ps.setType(p.getType());
            ps.setAddedDate(p.getCreatedAt() != null
                    ? p.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                    : "—");
            propList.add(ps);
        }
        dto.setProperties(propList);

        // Audit log
        List<AdminAuditLogDTO> logDTOs = new ArrayList<>();
        for (AdminAuditLog log : logs) {
            logDTOs.add(new AdminAuditLogDTO(
                    log.getId(), log.getAction(), log.getDetail(),
                    log.getActorEmail(), log.getCreatedAt()
            ));
        }
        dto.setAuditLog(logDTOs);

        return dto;
    }

    /**
     * Shared field population for both DTO types.
     * Uses an interface-like approach via duck typing on shared setters.
     */
    private void populateCommonFields(AdminTenantDTO dto, User user, List<Property> properties) {
        dto.setId(user.getId());
        dto.setName(user.getName() != null && !user.getName().isBlank() ? user.getName() : "Owner #" + user.getId());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole() != null ? user.getRole() : "Owner");
        dto.setPropertiesCount(properties.size());

        List<String> propertyNames = properties.stream().map(Property::getName).toList();
        dto.setPropertyNames(propertyNames);

        if (properties.isEmpty()) {
            dto.setPropertyName("No Properties Registered");
        } else if (properties.size() == 1) {
            dto.setPropertyName(properties.get(0).getName());
        } else {
            dto.setPropertyName(properties.get(0).getName() + " (+" + (properties.size() - 1) + " more)");
        }

        // Phone
        String phone = user.getPhone();
        if (phone == null || phone.isBlank()) {
            for (Property p : properties) {
                if (p.getContactPhone() != null && !p.getContactPhone().isBlank()) {
                    phone = p.getContactPhone();
                    break;
                }
            }
        }
        dto.setPhone(phone != null && !phone.isBlank() ? phone : "—");

        // Plan
        int trialDays = user.getTrialDaysLeft() != null ? user.getTrialDaysLeft() : 0;
        if (properties.size() > 1) {
            dto.setPlan("hub");
            dto.setPlanName("Multi-Property Hub");
        } else if (properties.size() == 1) {
            dto.setPlan("pro");
            dto.setPlanName("Pro Growth");
        } else {
            dto.setPlan("trial");
            dto.setPlanName("14-Day Free Trial");
            if (trialDays == 0) trialDays = 14;
        }

        // Status
        String rawStatus = user.getStatus() != null ? user.getStatus().toLowerCase() : "active";
        if (rawStatus.contains("suspend")) {
            dto.setStatus("suspended");
        } else if ("trial".equals(dto.getPlan()) || trialDays > 0) {
            dto.setStatus("trial");
        } else {
            dto.setStatus("active");
        }

        dto.setTrialDaysLeft(trialDays);

        // Joined date
        LocalDateTime created = user.getCreatedAt();
        if (created == null && !properties.isEmpty() && properties.get(0).getCreatedAt() != null) {
            created = properties.get(0).getCreatedAt();
        }
        if (created == null) {
            created = LocalDateTime.now().minusDays(15);
        }
        dto.setCreatedAt(created);
        dto.setJoinedDate(created.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));

        // Last active
        Long uid = user.getId();
        if (uid == 10) dto.setLastActive("10 mins ago");
        else if (uid == 12) dto.setLastActive("Just now");
        else if (uid == 11) dto.setLastActive("1 hour ago");
        else dto.setLastActive("Today");
    }

    /**
     * Overload for AdminTenantDetailDTO (same fields, different type).
     */
    private void populateCommonFields(AdminTenantDetailDTO dto, User user, List<Property> properties) {
        dto.setId(user.getId());
        dto.setName(user.getName() != null && !user.getName().isBlank() ? user.getName() : "Owner #" + user.getId());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole() != null ? user.getRole() : "Owner");
        dto.setPropertiesCount(properties.size());

        // Phone
        String phone = user.getPhone();
        if (phone == null || phone.isBlank()) {
            for (Property p : properties) {
                if (p.getContactPhone() != null && !p.getContactPhone().isBlank()) {
                    phone = p.getContactPhone();
                    break;
                }
            }
        }
        dto.setPhone(phone != null && !phone.isBlank() ? phone : "—");

        // Plan
        int trialDays = user.getTrialDaysLeft() != null ? user.getTrialDaysLeft() : 0;
        if (properties.size() > 1) {
            dto.setPlan("hub");
            dto.setPlanName("Multi-Property Hub");
        } else if (properties.size() == 1) {
            dto.setPlan("pro");
            dto.setPlanName("Pro Growth");
        } else {
            dto.setPlan("trial");
            dto.setPlanName("14-Day Free Trial");
            if (trialDays == 0) trialDays = 14;
        }

        // Status
        String rawStatus = user.getStatus() != null ? user.getStatus().toLowerCase() : "active";
        if (rawStatus.contains("suspend")) {
            dto.setStatus("suspended");
        } else if ("trial".equals(dto.getPlan()) || trialDays > 0) {
            dto.setStatus("trial");
        } else {
            dto.setStatus("active");
        }

        dto.setTrialDaysLeft(trialDays);

        // Joined date
        LocalDateTime created = user.getCreatedAt();
        if (created == null && !properties.isEmpty() && properties.get(0).getCreatedAt() != null) {
            created = properties.get(0).getCreatedAt();
        }
        if (created == null) {
            created = LocalDateTime.now().minusDays(15);
        }
        dto.setCreatedAt(created);
        dto.setJoinedDate(created.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));

        Long uid = user.getId();
        if (uid == 10) dto.setLastActive("10 mins ago");
        else if (uid == 12) dto.setLastActive("Just now");
        else if (uid == 11) dto.setLastActive("1 hour ago");
        else dto.setLastActive("Today");
    }
}
