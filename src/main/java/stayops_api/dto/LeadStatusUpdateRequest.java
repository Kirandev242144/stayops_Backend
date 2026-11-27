package stayops_api.dto;

import jakarta.validation.constraints.NotBlank;

public class LeadStatusUpdateRequest {

    @NotBlank(message = "Status cannot be blank")
    private String status;

    private String note;

    public LeadStatusUpdateRequest() {
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
