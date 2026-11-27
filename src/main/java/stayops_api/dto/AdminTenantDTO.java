package stayops_api.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AdminTenantDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private int propertiesCount;
    private String propertyName;
    private List<String> propertyNames;
    private String plan;
    private String planName;
    private String status;
    private int trialDaysLeft;
    private String joinedDate;
    private String lastActive;
    private LocalDateTime createdAt;

    public AdminTenantDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getPropertiesCount() {
        return propertiesCount;
    }

    public void setPropertiesCount(int propertiesCount) {
        this.propertiesCount = propertiesCount;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public List<String> getPropertyNames() {
        return propertyNames;
    }

    public void setPropertyNames(List<String> propertyNames) {
        this.propertyNames = propertyNames;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTrialDaysLeft() {
        return trialDaysLeft;
    }

    public void setTrialDaysLeft(int trialDaysLeft) {
        this.trialDaysLeft = trialDaysLeft;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getLastActive() {
        return lastActive;
    }

    public void setLastActive(String lastActive) {
        this.lastActive = lastActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
