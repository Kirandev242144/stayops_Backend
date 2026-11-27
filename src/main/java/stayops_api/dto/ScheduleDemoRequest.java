package stayops_api.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ScheduleDemoRequest {

    @NotNull(message = "Demo date and time is required")
    private LocalDateTime demoScheduledAt;

    private String demoTimeZone = "Asia/Kolkata";
    private String demoMeetingUrl;
    private String note;

    public ScheduleDemoRequest() {
    }

    public LocalDateTime getDemoScheduledAt() { return demoScheduledAt; }
    public void setDemoScheduledAt(LocalDateTime demoScheduledAt) { this.demoScheduledAt = demoScheduledAt; }

    public String getDemoTimeZone() { return demoTimeZone; }
    public void setDemoTimeZone(String demoTimeZone) { this.demoTimeZone = demoTimeZone; }

    public String getDemoMeetingUrl() { return demoMeetingUrl; }
    public void setDemoMeetingUrl(String demoMeetingUrl) { this.demoMeetingUrl = demoMeetingUrl; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
