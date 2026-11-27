package stayops_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_housekeeping_status")
public class RoomHousekeepingStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "property_id", nullable = false)
    private Long propertyId = 1L;

    @Column(name = "room_id", nullable = false, length = 100)
    private String roomId;

    @Column(nullable = false, length = 50)
    private String status = "CLEAN"; // CLEAN, DIRTY, IN_PROGRESS, INSPECTED, OUT_OF_ORDER

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "photo_proof_url", columnDefinition = "LONGTEXT")
    private String photoProofUrl;

    @Column(name = "cleaned_by", length = 100)
    private String cleanedBy;

    @Column(name = "assigned_to", length = 100)
    private String assignedTo;

    @Column(name = "task_type", length = 50)
    private String taskType = "TURNOVER"; // TURNOVER, STAYOVER, DEEP_CLEAN, INSPECTION, MAINTENANCE

    @Column(length = 50)
    private String priority = "NORMAL"; // URGENT, HIGH, NORMAL, LOW

    @Column(name = "checklist_json", columnDefinition = "TEXT")
    private String checklistJson;

    @Column(name = "maintenance_issue", columnDefinition = "TEXT")
    private String maintenanceIssue;

    @Column(name = "maintenance_severity", length = 50)
    private String maintenanceSeverity; // COSMETIC, MODERATE, CRITICAL

    @Column(name = "last_cleaned_at")
    private LocalDateTime lastCleanedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public RoomHousekeepingStatus() {}

    public RoomHousekeepingStatus(Long propertyId, String roomId, String status, String notes) {
        this.propertyId = propertyId;
        this.roomId = roomId;
        this.status = status;
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
        if ("CLEAN".equalsIgnoreCase(status)) {
            this.lastCleanedAt = LocalDateTime.now();
        }
    }

    @PrePersist
    @PreUpdate
    public void onSave() {
        this.updatedAt = LocalDateTime.now();
        if ("CLEAN".equalsIgnoreCase(this.status) && this.lastCleanedAt == null) {
            this.lastCleanedAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
        if ("CLEAN".equalsIgnoreCase(status) || "INSPECTED".equalsIgnoreCase(status)) {
            this.lastCleanedAt = LocalDateTime.now();
        }
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getPhotoProofUrl() { return photoProofUrl; }
    public void setPhotoProofUrl(String photoProofUrl) { this.photoProofUrl = photoProofUrl; }

    public String getCleanedBy() { return cleanedBy; }
    public void setCleanedBy(String cleanedBy) { this.cleanedBy = cleanedBy; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getChecklistJson() { return checklistJson; }
    public void setChecklistJson(String checklistJson) { this.checklistJson = checklistJson; }

    public String getMaintenanceIssue() { return maintenanceIssue; }
    public void setMaintenanceIssue(String maintenanceIssue) { this.maintenanceIssue = maintenanceIssue; }

    public String getMaintenanceSeverity() { return maintenanceSeverity; }
    public void setMaintenanceSeverity(String maintenanceSeverity) { this.maintenanceSeverity = maintenanceSeverity; }

    public LocalDateTime getLastCleanedAt() { return lastCleanedAt; }
    public void setLastCleanedAt(LocalDateTime lastCleanedAt) { this.lastCleanedAt = lastCleanedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
