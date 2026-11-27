package stayops_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lead_activity")
public class LeadActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lead_id", nullable = false)
    private Long leadId;

    @Column(name = "actor_admin_email", length = 150)
    private String actorAdminEmail;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "old_value", length = 255)
    private String oldValue;

    @Column(name = "new_value", length = 255)
    private String newValue;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public LeadActivity() {
    }

    public LeadActivity(Long leadId, String actorAdminEmail, String eventType, String oldValue, String newValue, String metadata) {
        this.leadId = leadId;
        this.actorAdminEmail = actorAdminEmail;
        this.eventType = eventType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.metadata = metadata;
        this.createdAt = LocalDateTime.now();
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
}
