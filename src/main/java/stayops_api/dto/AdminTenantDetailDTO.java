package stayops_api.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AdminTenantDetailDTO {

    // ── Overview fields ──────────────────────────────────────────────
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String plan;
    private String planName;
    private String status;
    private int trialDaysLeft;
    private String joinedDate;
    private String lastActive;
    private LocalDateTime createdAt;
    private int propertiesCount;

    // ── Extended fields ──────────────────────────────────────────────
    private List<PropertySummary> properties;
    private List<AdminAuditLogDTO> auditLog;

    public AdminTenantDetailDTO() {
    }

    // ── Property summary inner class ─────────────────────────────────
    public static class PropertySummary {
        private Long id;
        private String name;
        private String city;
        private String state;
        private Integer rooms;
        private String status;
        private String addedDate;
        private String type;

        public PropertySummary() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public Integer getRooms() { return rooms; }
        public void setRooms(Integer rooms) { this.rooms = rooms; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getAddedDate() { return addedDate; }
        public void setAddedDate(String addedDate) { this.addedDate = addedDate; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    // ── Getters & Setters ────────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getTrialDaysLeft() { return trialDaysLeft; }
    public void setTrialDaysLeft(int trialDaysLeft) { this.trialDaysLeft = trialDaysLeft; }
    public String getJoinedDate() { return joinedDate; }
    public void setJoinedDate(String joinedDate) { this.joinedDate = joinedDate; }
    public String getLastActive() { return lastActive; }
    public void setLastActive(String lastActive) { this.lastActive = lastActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public int getPropertiesCount() { return propertiesCount; }
    public void setPropertiesCount(int propertiesCount) { this.propertiesCount = propertiesCount; }
    public List<PropertySummary> getProperties() { return properties; }
    public void setProperties(List<PropertySummary> properties) { this.properties = properties; }
    public List<AdminAuditLogDTO> getAuditLog() { return auditLog; }
    public void setAuditLog(List<AdminAuditLogDTO> auditLog) { this.auditLog = auditLog; }
}
