package stayops_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String status = "Active";

    @Column(length = 50)
    private String role = "Owner";

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "plan", length = 50)
    private String plan = "pro";

    @Column(name = "trial_days_left")
    private Integer trialDaysLeft = 0;

    @Column(name = "custom_max_properties")
    private Integer customMaxProperties;

    @Column(name = "custom_max_rooms")
    private Integer customMaxRooms;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.status = "Active";
        this.role = "Owner";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status != null ? status : "Active";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role != null ? role : "Owner";
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPlan() {
        return plan != null ? plan : "pro";
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public Integer getTrialDaysLeft() {
        return trialDaysLeft != null ? trialDaysLeft : 0;
    }

    public void setTrialDaysLeft(Integer trialDaysLeft) {
        this.trialDaysLeft = trialDaysLeft;
    }

    public Integer getCustomMaxProperties() {
        return customMaxProperties;
    }

    public void setCustomMaxProperties(Integer customMaxProperties) {
        this.customMaxProperties = customMaxProperties;
    }

    public Integer getCustomMaxRooms() {
        return customMaxRooms;
    }

    public void setCustomMaxRooms(Integer customMaxRooms) {
        this.customMaxRooms = customMaxRooms;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}