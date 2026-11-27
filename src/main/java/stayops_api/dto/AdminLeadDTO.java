package stayops_api.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AdminLeadDTO {

    private Long id;
    private String fullName;
    private String businessName;
    private String email;
    private String phone;
    private Integer propertyCount;
    private String propertyType;
    private String city;
    private String state;
    private String message;
    private String preferredContactMethod;
    private String status;
    private String source;
    private String assignedAdminEmail;
    private LocalDateTime demoScheduledAt;
    private String demoScheduledAtFormatted;
    private String demoTimeZone;
    private String demoMeetingUrl;
    private LocalDateTime nextFollowUpAt;
    private String nextFollowUpAtFormatted;
    private boolean isFollowUpDue;
    private Long convertedTenantId;
    private LocalDateTime convertedAt;
    private String convertedAtFormatted;
    private LocalDateTime createdAt;
    private String createdAtFormatted;
    private LocalDateTime updatedAt;

    // Optional detailed lists populated on single-lead detail query
    private List<LeadNoteDTO> notes;
    private List<LeadActivityDTO> activity;

    public AdminLeadDTO() {
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

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

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

    public String getDemoScheduledAtFormatted() { return demoScheduledAtFormatted; }
    public void setDemoScheduledAtFormatted(String demoScheduledAtFormatted) { this.demoScheduledAtFormatted = demoScheduledAtFormatted; }

    public String getDemoTimeZone() { return demoTimeZone; }
    public void setDemoTimeZone(String demoTimeZone) { this.demoTimeZone = demoTimeZone; }

    public String getDemoMeetingUrl() { return demoMeetingUrl; }
    public void setDemoMeetingUrl(String demoMeetingUrl) { this.demoMeetingUrl = demoMeetingUrl; }

    public LocalDateTime getNextFollowUpAt() { return nextFollowUpAt; }
    public void setNextFollowUpAt(LocalDateTime nextFollowUpAt) { this.nextFollowUpAt = nextFollowUpAt; }

    public String getNextFollowUpAtFormatted() { return nextFollowUpAtFormatted; }
    public void setNextFollowUpAtFormatted(String nextFollowUpAtFormatted) { this.nextFollowUpAtFormatted = nextFollowUpAtFormatted; }

    public boolean isFollowUpDue() { return isFollowUpDue; }
    public void setFollowUpDue(boolean followUpDue) { isFollowUpDue = followUpDue; }

    public Long getConvertedTenantId() { return convertedTenantId; }
    public void setConvertedTenantId(Long convertedTenantId) { this.convertedTenantId = convertedTenantId; }

    public LocalDateTime getConvertedAt() { return convertedAt; }
    public void setConvertedAt(LocalDateTime convertedAt) { this.convertedAt = convertedAt; }

    public String getConvertedAtFormatted() { return convertedAtFormatted; }
    public void setConvertedAtFormatted(String convertedAtFormatted) { this.convertedAtFormatted = convertedAtFormatted; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedAtFormatted() { return createdAtFormatted; }
    public void setCreatedAtFormatted(String createdAtFormatted) { this.createdAtFormatted = createdAtFormatted; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<LeadNoteDTO> getNotes() { return notes; }
    public void setNotes(List<LeadNoteDTO> notes) { this.notes = notes; }

    public List<LeadActivityDTO> getActivity() { return activity; }
    public void setActivity(List<LeadActivityDTO> activity) { this.activity = activity; }
}
