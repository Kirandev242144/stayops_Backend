package stayops_api.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import stayops_api.entity.TeamMember;

import java.time.LocalDateTime;
import java.util.*;

public class TeamMemberDto {

    private Long id;
    private Long ownerId;
    private String name;
    private String mobileNumber;
    private String pin;
    private String password;
    private String role;
    private List<String> assignedProperties;
    private List<String> assignedPropertyNames;
    private Map<String, Boolean> permissions;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TeamMemberDto() {
    }

    public static TeamMemberDto fromEntity(TeamMember member, ObjectMapper objectMapper) {
        if (member == null) return null;
        TeamMemberDto dto = new TeamMemberDto();
        dto.setId(member.getId());
        dto.setOwnerId(member.getOwnerId());
        dto.setName(member.getName());
        dto.setMobileNumber(member.getMobileNumber());
        dto.setPin(member.getPin());
        dto.setPassword(member.getPassword() != null ? member.getPassword() : member.getPin());
        dto.setRole(member.getRole());
        dto.setStatus(member.getStatus());
        dto.setCreatedAt(member.getCreatedAt());
        dto.setUpdatedAt(member.getUpdatedAt());

        // Parse assignedProperties
        if (member.getAssignedProperties() != null && !member.getAssignedProperties().trim().isEmpty()) {
            try {
                List<String> props = objectMapper.readValue(member.getAssignedProperties(), new TypeReference<List<String>>() {});
                dto.setAssignedProperties(props);
            } catch (Exception e) {
                dto.setAssignedProperties(Collections.singletonList(member.getAssignedProperties()));
            }
        } else {
            dto.setAssignedProperties(Collections.singletonList("all"));
        }

        // Parse assignedPropertyNames
        if (member.getAssignedPropertyNames() != null && !member.getAssignedPropertyNames().trim().isEmpty()) {
            try {
                List<String> names = objectMapper.readValue(member.getAssignedPropertyNames(), new TypeReference<List<String>>() {});
                dto.setAssignedPropertyNames(names);
            } catch (Exception e) {
                dto.setAssignedPropertyNames(Collections.singletonList(member.getAssignedPropertyNames()));
            }
        } else {
            dto.setAssignedPropertyNames(Collections.singletonList("All Properties"));
        }

        // Parse permissions
        if (member.getPermissions() != null && !member.getPermissions().trim().isEmpty()) {
            try {
                Map<String, Boolean> perms = objectMapper.readValue(member.getPermissions(), new TypeReference<Map<String, Boolean>>() {});
                dto.setPermissions(perms);
            } catch (Exception e) {
                dto.setPermissions(getDefaultPermissions(member.getRole()));
            }
        } else {
            dto.setPermissions(getDefaultPermissions(member.getRole()));
        }

        return dto;
    }

    public void updateEntity(TeamMember member, ObjectMapper objectMapper) {
        if (this.name != null && !this.name.trim().isEmpty()) {
            member.setName(this.name.trim());
        }
        if (this.mobileNumber != null && !this.mobileNumber.trim().isEmpty()) {
            member.setMobileNumber(this.mobileNumber.trim());
        }
        String accessPin = this.pin != null && !this.pin.trim().isEmpty() ? this.pin.trim() : (this.password != null ? this.password.trim() : null);
        if (accessPin != null) {
            member.setPin(accessPin);
            member.setPassword(accessPin);
        }
        if (this.role != null && !this.role.trim().isEmpty()) {
            member.setRole(this.role.trim());
        }
        if (this.status != null && !this.status.trim().isEmpty()) {
            member.setStatus(this.status.trim());
        }

        try {
            if (this.assignedProperties != null && !this.assignedProperties.isEmpty()) {
                member.setAssignedProperties(objectMapper.writeValueAsString(this.assignedProperties));
            }
            if (this.assignedPropertyNames != null && !this.assignedPropertyNames.isEmpty()) {
                member.setAssignedPropertyNames(objectMapper.writeValueAsString(this.assignedPropertyNames));
            }
            if (this.permissions != null && !this.permissions.isEmpty()) {
                member.setPermissions(objectMapper.writeValueAsString(this.permissions));
            }
        } catch (Exception ignored) {
        }
    }

    public static Map<String, Boolean> getDefaultPermissions(String role) {
        Map<String, Boolean> map = new HashMap<>();
        boolean isManager = role != null && role.equalsIgnoreCase("Property Manager");
        map.put("photoEvidence", true);
        map.put("pettyCash", true);
        map.put("mealLogging", true);
        map.put("manageBookings", isManager);
        return map;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getAssignedProperties() {
        return assignedProperties;
    }

    public void setAssignedProperties(List<String> assignedProperties) {
        this.assignedProperties = assignedProperties;
    }

    public List<String> getAssignedPropertyNames() {
        return assignedPropertyNames;
    }

    public void setAssignedPropertyNames(List<String> assignedPropertyNames) {
        this.assignedPropertyNames = assignedPropertyNames;
    }

    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, Boolean> permissions) {
        this.permissions = permissions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
