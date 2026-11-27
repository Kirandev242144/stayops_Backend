package stayops_api.dto;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class AdminAuditLogDTO {

    private Long id;
    private String action;
    private String detail;
    private String actorEmail;
    private String timestamp;

    public AdminAuditLogDTO() {
    }

    public AdminAuditLogDTO(Long id, String action, String detail, String actorEmail, LocalDateTime createdAt) {
        this.id = id;
        this.action = action;
        this.detail = detail;
        this.actorEmail = actorEmail;
        this.timestamp = createdAt != null
                ? createdAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"))
                : "—";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getActorEmail() { return actorEmail; }
    public void setActorEmail(String actorEmail) { this.actorEmail = actorEmail; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
