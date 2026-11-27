package stayops_api.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ScheduleFollowUpRequest {

    @NotNull(message = "Follow up date and time is required")
    private LocalDateTime nextFollowUpAt;

    private String note;

    public ScheduleFollowUpRequest() {
    }

    public LocalDateTime getNextFollowUpAt() { return nextFollowUpAt; }
    public void setNextFollowUpAt(LocalDateTime nextFollowUpAt) { this.nextFollowUpAt = nextFollowUpAt; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
