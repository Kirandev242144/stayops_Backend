package stayops_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "reservation_rooms")
public class ReservationRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    @JsonIgnore
    private Reservation reservation;

    @Column(name = "room_unit")
    private String roomUnit;

    @Column(name = "room_type_id")
    private Long roomTypeId;

    @Column(name = "room_type_name")
    private String roomTypeName;

    @Column(name = "nightly_rate")
    private BigDecimal nightlyRate = BigDecimal.ZERO;

    @Column(name = "capacity")
    private Integer capacity = 1;

    public ReservationRoom() {
    }

    public ReservationRoom(String roomUnit, Long roomTypeId, String roomTypeName, BigDecimal nightlyRate, Integer capacity) {
        this.roomUnit = roomUnit;
        this.roomTypeId = roomTypeId;
        this.roomTypeName = roomTypeName;
        this.nightlyRate = nightlyRate;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public String getRoomUnit() {
        return roomUnit;
    }

    public void setRoomUnit(String roomUnit) {
        this.roomUnit = roomUnit;
    }

    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    public BigDecimal getNightlyRate() {
        return nightlyRate;
    }

    public void setNightlyRate(BigDecimal nightlyRate) {
        this.nightlyRate = nightlyRate;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
