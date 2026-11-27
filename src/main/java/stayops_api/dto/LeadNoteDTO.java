package stayops_api.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LeadNoteDTO {

    private Long id;
    private Long leadId;
    private String authorAdminEmail;
    private String note;
    private LocalDateTime createdAt;
    private String createdAtFormatted;

    public LeadNoteDTO() {
    }

    public LeadNoteDTO(Long id, Long leadId, String authorAdminEmail, String note, LocalDateTime createdAt) {
        this.id = id;
        this.leadId = leadId;
        this.authorAdminEmail = authorAdminEmail;
        this.note = note;
        this.createdAt = createdAt;
        this.createdAtFormatted = createdAt != null
                ? createdAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"))
                : "—";
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

    public String getCreatedAtFormatted() { return createdAtFormatted; }
    public void setCreatedAtFormatted(String createdAtFormatted) { this.createdAtFormatted = createdAtFormatted; }
}
