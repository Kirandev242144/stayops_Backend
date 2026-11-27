package stayops_api.dto;

import java.math.BigDecimal;

public class ReservationRoomDTO {

    private Long id;
    private String roomUnit;
    private Long roomTypeId;
    private String roomTypeName;
    private BigDecimal nightlyRate;
    private Integer capacity;

    public ReservationRoomDTO() {
    }

    public ReservationRoomDTO(Long id, String roomUnit, Long roomTypeId, String roomTypeName, BigDecimal nightlyRate, Integer capacity) {
        this.id = id;
        this.roomUnit = roomUnit;
        this.roomTypeId = roomTypeId;
        this.roomTypeName = roomTypeName;
        this.nightlyRate = nightlyRate;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
