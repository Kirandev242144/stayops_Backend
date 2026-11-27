package stayops_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_leads")
public class SalesLead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "business_name", nullable = false, length = 150)
    private String businessName;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(name = "normalized_email", nullable = false, length = 150)
    private String normalizedEmail;

    @Column(nullable = false, length = 50)
    private String phone;

    @Column(name = "normalized_phone", nullable = false, length = 50)
    private String normalizedPhone;

    @Column(name = "property_count", nullable = false)
    private Integer propertyCount = 1;

    @Column(name = "property_type", length = 100)
    private String propertyType;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "preferred_contact_method", length = 50)
    private String preferredContactMethod = "WhatsApp";

    @Column(nullable = false, length = 50)
    private String status = "NEW";

    @Column(length = 100)
    private String source = "Marketing Website";

    @Column(name = "assigned_admin_email", length = 150)
    private String assignedAdminEmail;

    @Column(name = "demo_scheduled_at")
    private LocalDateTime demoScheduledAt;

    @Column(name = "demo_time_zone", length = 50)
    private String demoTimeZone = "Asia/Kolkata";

    @Column(name = "demo_meeting_url", length = 255)
    private String demoMeetingUrl;

    @Column(name = "next_follow_up_at")
    private LocalDateTime nextFollowUpAt;

    @Column(name = "converted_tenant_id")
    private Long convertedTenantId;

    @Column(name = "converted_at")
    private LocalDateTime convertedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public SalesLead() {
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNormalizedEmail() { return normalizedEmail; }
    public void setNormalizedEmail(String normalizedEmail) { this.normalizedEmail = normalizedEmail; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getNormalizedPhone() { return normalizedPhone; }
    public void setNormalizedPhone(String normalizedPhone) { this.normalizedPhone = normalizedPhone; }

    public Integer getPropertyCount() { return propertyCount; }
    public void setPropertyCount(Integer propertyCount) { this.propertyCount = propertyCount; }

    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPreferredContactMethod() { return preferredContactMethod; }
    public void setPreferredContactMethod(String preferredContactMethod) { this.preferredContactMethod = preferredContactMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getAssignedAdminEmail() { return assignedAdminEmail; }
    public void setAssignedAdminEmail(String assignedAdminEmail) { this.assignedAdminEmail = assignedAdminEmail; }

    public LocalDateTime getDemoScheduledAt() { return demoScheduledAt; }
    public void setDemoScheduledAt(LocalDateTime demoScheduledAt) { this.demoScheduledAt = demoScheduledAt; }

    public String getDemoTimeZone() { return demoTimeZone; }
    public void setDemoTimeZone(String demoTimeZone) { this.demoTimeZone = demoTimeZone; }

    public String getDemoMeetingUrl() { return demoMeetingUrl; }
    public void setDemoMeetingUrl(String demoMeetingUrl) { this.demoMeetingUrl = demoMeetingUrl; }

    public LocalDateTime getNextFollowUpAt() { return nextFollowUpAt; }
    public void setNextFollowUpAt(LocalDateTime nextFollowUpAt) { this.nextFollowUpAt = nextFollowUpAt; }

    public Long getConvertedTenantId() { return convertedTenantId; }
    public void setConvertedTenantId(Long convertedTenantId) { this.convertedTenantId = convertedTenantId; }

    public LocalDateTime getConvertedAt() { return convertedAt; }
    public void setConvertedAt(LocalDateTime convertedAt) { this.convertedAt = convertedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
