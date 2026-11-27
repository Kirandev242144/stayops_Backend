package stayops_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationResponse {

    private Long id;

    private String bookingRef;

    private Long propertyId;

    private String guestName;

    private String guestEmail;

    private String guestPhone;

    private String propertyName;

    private String roomUnit;

    private LocalDate checkIn;

    private String checkInTime;

    private LocalDate checkOut;

    private String checkOutTime;

    private Integer guestCount;

    private BigDecimal totalAmount;

    private String paymentStatus;

    private BigDecimal amountPaid;

    private String bookingSource;

    private String notes;

    private String razorpayPaymentId;

    private BigDecimal roomBaseTotal;

    private BigDecimal roomSubtotal;

    private BigDecimal roomServiceCharge;

    private BigDecimal roomGstAmount;

    private String taxSettingsSnapshot;

    private String idType;
    private String idNumber;
    private String idDocumentUrl;
    private String idDocuments;
    private String guestCity;
    private String guestState;

    private java.util.List<ReservationRoomDTO> allocatedRooms;

    private String status;

    private LocalDateTime createdAt;

    public java.util.List<ReservationRoomDTO> getAllocatedRooms() {
        return allocatedRooms;
    }

    public void setAllocatedRooms(java.util.List<ReservationRoomDTO> allocatedRooms) {
        this.allocatedRooms = allocatedRooms;
    }

    public ReservationResponse() {
    }


    public ReservationResponse(
            Long id,
            String bookingRef,
            String guestName,
            String guestEmail,
            String guestPhone,
            String propertyName,
            String roomUnit,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer guestCount,
            BigDecimal totalAmount,
            String paymentStatus,
            BigDecimal amountPaid,
            String bookingSource,
            String notes,
            String razorpayPaymentId,
            String status,
            LocalDateTime createdAt
    ) {
        this(id, bookingRef, null, guestName, guestEmail, guestPhone, propertyName, roomUnit, checkIn, "14:00", checkOut, "11:00", guestCount, totalAmount, paymentStatus, amountPaid, bookingSource, notes, razorpayPaymentId, status, createdAt);
    }

    public ReservationResponse(
            Long id,
            String bookingRef,
            String guestName,
            String guestEmail,
            String guestPhone,
            String propertyName,
            String roomUnit,
            LocalDate checkIn,
            String checkInTime,
            LocalDate checkOut,
            String checkOutTime,
            Integer guestCount,
            BigDecimal totalAmount,
            String paymentStatus,
            BigDecimal amountPaid,
            String bookingSource,
            String notes,
            String razorpayPaymentId,
            String status,
            LocalDateTime createdAt
    ) {
        this(id, bookingRef, null, guestName, guestEmail, guestPhone, propertyName, roomUnit, checkIn, checkInTime, checkOut, checkOutTime, guestCount, totalAmount, paymentStatus, amountPaid, bookingSource, notes, razorpayPaymentId, status, createdAt);
    }

    public ReservationResponse(
            Long id,
            String bookingRef,
            Long propertyId,
            String guestName,
            String guestEmail,
            String guestPhone,
            String propertyName,
            String roomUnit,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer guestCount,
            BigDecimal totalAmount,
            String paymentStatus,
            BigDecimal amountPaid,
            String bookingSource,
            String notes,
            String razorpayPaymentId,
            String status,
            LocalDateTime createdAt
    ) {
        this(id, bookingRef, propertyId, guestName, guestEmail, guestPhone, propertyName, roomUnit, checkIn, "14:00", checkOut, "11:00", guestCount, totalAmount, paymentStatus, amountPaid, bookingSource, notes, razorpayPaymentId, status, createdAt);
    }

    public ReservationResponse(
            Long id,
            String bookingRef,
            Long propertyId,
            String guestName,
            String guestEmail,
            String guestPhone,
            String propertyName,
            String roomUnit,
            LocalDate checkIn,
            String checkInTime,
            LocalDate checkOut,
            String checkOutTime,
            Integer guestCount,
            BigDecimal totalAmount,
            String paymentStatus,
            BigDecimal amountPaid,
            String bookingSource,
            String notes,
            String razorpayPaymentId,
            String status,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.bookingRef = bookingRef;
        this.propertyId = propertyId;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.guestPhone = guestPhone;
        this.propertyName = propertyName;
        this.roomUnit = roomUnit;
        this.checkIn = checkIn;
        this.checkInTime = checkInTime != null ? checkInTime : "14:00";
        this.checkOut = checkOut;
        this.checkOutTime = checkOutTime != null ? checkOutTime : "11:00";
        this.guestCount = guestCount;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.amountPaid = amountPaid;
        this.bookingSource = bookingSource;
        this.notes = notes;
        this.razorpayPaymentId = razorpayPaymentId;
        this.status = status;
        this.createdAt = createdAt;
    }



    public Long getId() {
        return id;
    }

    public Long getPropertyId() {
        return propertyId;
    }


    public String getBookingRef() {
        return bookingRef;
    }


    public String getGuestName() {
        return guestName;
    }


    public String getGuestEmail() {
        return guestEmail;
    }


    public String getGuestPhone() {
        return guestPhone;
    }


    public String getPropertyName() {
        return propertyName;
    }


    public String getRoomUnit() {
        return roomUnit;
    }


    public LocalDate getCheckIn() {
        return checkIn;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }



    public Integer getGuestCount() {
        return guestCount;
    }


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }


    public String getPaymentStatus() {
        return paymentStatus;
    }


    public BigDecimal getAmountPaid() {
        return amountPaid;
    }


    public String getBookingSource() {
        return bookingSource;
    }


    public String getNotes() {
        return notes;
    }


    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public BigDecimal getRoomBaseTotal() {
        return roomBaseTotal;
    }

    public void setRoomBaseTotal(BigDecimal roomBaseTotal) {
        this.roomBaseTotal = roomBaseTotal;
    }

    public BigDecimal getRoomSubtotal() {
        return roomSubtotal;
    }

    public void setRoomSubtotal(BigDecimal roomSubtotal) {
        this.roomSubtotal = roomSubtotal;
    }

    public BigDecimal getRoomServiceCharge() {
        return roomServiceCharge;
    }

    public void setRoomServiceCharge(BigDecimal roomServiceCharge) {
        this.roomServiceCharge = roomServiceCharge;
    }

    public BigDecimal getRoomGstAmount() {
        return roomGstAmount;
    }

    public void setRoomGstAmount(BigDecimal roomGstAmount) {
        this.roomGstAmount = roomGstAmount;
    }

    public String getTaxSettingsSnapshot() {
        return taxSettingsSnapshot;
    }

    public void setTaxSettingsSnapshot(String taxSettingsSnapshot) {
        this.taxSettingsSnapshot = taxSettingsSnapshot;
    }


    public String getStatus() {
        return status;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIdDocumentUrl() {
        return idDocumentUrl;
    }

    public void setIdDocumentUrl(String idDocumentUrl) {
        this.idDocumentUrl = idDocumentUrl;
    }

    public String getIdDocuments() {
        return idDocuments;
    }

    public void setIdDocuments(String idDocuments) {
        this.idDocuments = idDocuments;
    }

    public String getGuestCity() {
        return guestCity;
    }

    public void setGuestCity(String guestCity) {
        this.guestCity = guestCity;
    }

    public String getGuestState() {
        return guestState;
    }

    public void setGuestState(String guestState) {
        this.guestState = guestState;
    }
}