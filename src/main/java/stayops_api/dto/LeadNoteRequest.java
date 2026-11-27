package stayops_api.dto;

import jakarta.validation.constraints.NotBlank;

public class LeadNoteRequest {

    @NotBlank(message = "Note content cannot be blank")
    private String note;

    public LeadNoteRequest() {
    }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
