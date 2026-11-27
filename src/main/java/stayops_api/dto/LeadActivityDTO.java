package stayops_api.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LeadActivityDTO {

    private Long id;
    private Long leadId;
    private String actorAdminEmail;
    private String eventType;
    private String oldValue;
    private String newValue;
    private String metadata;
    private LocalDateTime createdAt;
    private String createdAtFormatted;

    public LeadActivityDTO() {
    }

    public LeadActivityDTO(Long id, Long leadId, String actorAdminEmail, String eventType, String oldValue, String newValue, String metadata, LocalDateTime createdAt) {
        this.id = id;
        this.leadId = leadId;
        this.actorAdminEmail = actorAdminEmail;
        this.eventType = eventType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.metadata = metadata;
        this.createdAt = createdAt;
        this.createdAtFormatted = createdAt != null
                ? createdAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"))
                : "—";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLeadId() { return leadId; }
    public void setLeadId(Long leadId) { this.leadId = leadId; }

    public String getActorAdminEmail() { return actorAdminEmail; }
    public void setActorAdminEmail(String actorAdminEmail) { this.actorAdminEmail = actorAdminEmail; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedAtFormatted() { return createdAtFormatted; }
    public void setCreatedAtFormatted(String createdAtFormatted) { this.createdAtFormatted = createdAtFormatted; }
}
