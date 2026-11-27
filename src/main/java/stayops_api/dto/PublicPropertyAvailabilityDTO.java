package stayops_api.dto;

import java.time.LocalDate;
import java.util.List;

public class PublicPropertyAvailabilityDTO {
    private Long propertyId;
    private List<BookedRange> bookedRanges;
    private List<BlockedRange> blockedRanges;

    public PublicPropertyAvailabilityDTO() {}

    public PublicPropertyAvailabilityDTO(Long propertyId, List<BookedRange> bookedRanges, List<BlockedRange> blockedRanges) {
        this.propertyId = propertyId;
        this.bookedRanges = bookedRanges;
        this.blockedRanges = blockedRanges;
    }

    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

    public List<BookedRange> getBookedRanges() { return bookedRanges; }
    public void setBookedRanges(List<BookedRange> bookedRanges) { this.bookedRanges = bookedRanges; }

    public List<BlockedRange> getBlockedRanges() { return blockedRanges; }
    public void setBlockedRanges(List<BlockedRange> blockedRanges) { this.blockedRanges = blockedRanges; }

    public static class BookedRange {
        private String roomUnit;
        private LocalDate checkIn;
        private LocalDate checkOut;
        private String status;

        public BookedRange() {}

        public BookedRange(String roomUnit, LocalDate checkIn, LocalDate checkOut, String status) {
            this.roomUnit = roomUnit;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.status = status;
        }

        public String getRoomUnit() { return roomUnit; }
        public void setRoomUnit(String roomUnit) { this.roomUnit = roomUnit; }

        public LocalDate getCheckIn() { return checkIn; }
        public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

        public LocalDate getCheckOut() { return checkOut; }
        public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class BlockedRange {
        private String roomId;
        private LocalDate startDate;
        private LocalDate endDate;
        private String category;

        public BlockedRange() {}

        public BlockedRange(String roomId, LocalDate startDate, LocalDate endDate, String category) {
            this.roomId = roomId;
            this.startDate = startDate;
            this.endDate = endDate;
            this.category = category;
        }

        public String getRoomId() { return roomId; }
        public void setRoomId(String roomId) { this.roomId = roomId; }

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }
}
