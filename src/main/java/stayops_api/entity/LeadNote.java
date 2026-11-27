package stayops_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lead_notes")
public class LeadNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lead_id", nullable = false)
    private Long leadId;

    @Column(name = "author_admin_email", nullable = false, length = 150)
    private String authorAdminEmail;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String note;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public LeadNote() {
    }

    public LeadNote(Long leadId, String authorAdminEmail, String note) {
        this.leadId = leadId;
        this.authorAdminEmail = authorAdminEmail;
        this.note = note;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLeadId() { return leadId; }
    public void setLeadId(Long leadId) { this.leadId = leadId; }

    public String getAuthorAdminEmail() { return authorAdminEmail; }
    public void setAuthorAdminEmail(String authorAdminEmail) { this.authorAdminEmail = authorAdminEmail; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
