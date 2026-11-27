package stayops_api.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_rate_overrides", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"property_id", "room_id", "date"})
})
public class DailyRateOverride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    @Column(name = "room_id", nullable = false)
    private String roomId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "override_rate", nullable = false)
    private BigDecimal overrideRate = BigDecimal.ZERO;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public DailyRateOverride() {
    }

    @PrePersist
    @PreUpdate
    protected void onSave() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getOverrideRate() {
        return overrideRate;
    }

    public void setOverrideRate(BigDecimal overrideRate) {
        this.overrideRate = overrideRate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
