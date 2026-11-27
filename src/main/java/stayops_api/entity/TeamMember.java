package stayops_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_members")
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId = 1L;

    @Column(nullable = false)
    private String name;

    @Column(name = "mobile_number", unique = true, nullable = false, length = 20)
    private String mobileNumber;

    @Column(length = 20, nullable = false)
    private String pin;

    @Column(length = 50)
    private String password;

    @Column(nullable = false, length = 100)
    private String role;

    @Column(name = "assigned_properties", columnDefinition = "TEXT")
    private String assignedProperties; // JSON Array string e.g. ["1", "2"]

    @Column(name = "assigned_property_names", columnDefinition = "TEXT")
    private String assignedPropertyNames; // JSON Array string e.g. ["Sri Nature Villa"]

    @Column(columnDefinition = "TEXT")
    private String permissions; // JSON Object string e.g. {"photoEvidence":true,...}

    @Column(length = 30, nullable = false)
    private String status = "Active";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public TeamMember() {
    }

    public TeamMember(Long ownerId, String name, String mobileNumber, String pin, String role) {
        this.ownerId = ownerId;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.pin = pin;
        this.password = pin;
        this.role = role;
        this.status = "Active";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
        this.password = pin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        if (this.pin == null || this.pin.isEmpty()) {
            this.pin = password;
        }
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAssignedProperties() {
        return assignedProperties;
    }

    public void setAssignedProperties(String assignedProperties) {
        this.assignedProperties = assignedProperties;
    }

    public String getAssignedPropertyNames() {
        return assignedPropertyNames;
    }

    public void setAssignedPropertyNames(String assignedPropertyNames) {
        this.assignedPropertyNames = assignedPropertyNames;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
