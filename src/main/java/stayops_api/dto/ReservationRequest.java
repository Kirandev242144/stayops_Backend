package stayops_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Guest Reservation Booking Creation Payload")
public class ReservationRequest {

    @Schema(description = "Property Owner User ID (optional)", example = "10")
    private Long userId;

    @Schema(description = "Primary Guest Full Name", example = "Arjun Kumar")
    private String guestName;

    @Schema(description = "Primary Guest Email", example = "arjun@example.com")
    private String guestEmail;

    @Schema(description = "Primary Guest Phone Number", example = "+91 9876543210")
    private String guestPhone;

    @Schema(description = "Property ID", example = "33")
    private Long propertyId;

    @Schema(description = "Room Unit or Category Name", example = "Garden View Cottage")
    private String roomUnit;

    @Schema(description = "Check-in Date (YYYY-MM-DD)", example = "2026-10-10")
    private LocalDate checkIn;

    @Schema(description = "Check-in Time", example = "12:00 PM")
    private String checkInTime;

    @Schema(description = "Check-out Date (YYYY-MM-DD)", example = "2026-10-12")
    private LocalDate checkOut;

    @Schema(description = "Check-out Time", example = "11:00 AM")
    private String checkOutTime;

    @Schema(description = "Total Guests Count", example = "2")
    private Integer guestCount;

    @Schema(description = "Total Booking Amount (INR)", example = "7000.00")
    private BigDecimal totalAmount;

    @Schema(description = "Payment Status (Paid, Partial Paid, Pay at Check-in)", example = "Pay at Check-in")
    private String paymentStatus;

    @Schema(description = "Advance Amount Paid", example = "0.00")
    private BigDecimal amountPaid;

    @Schema(description = "Booking Channel (Direct Website, Front Desk, Airbnb, Booking.com)", example = "Direct Website")
    private String bookingSource;

    @Schema(description = "Special Requests / Notes", example = "Late check-in expected around 6 PM")
    private String notes;

    @Schema(description = "Razorpay Payment ID (if paid online)", example = "pay_P1x99zKwL210")
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

    public java.util.List<ReservationRoomDTO> getAllocatedRooms() {
        return allocatedRooms;
    }

    public void setAllocatedRooms(java.util.List<ReservationRoomDTO> allocatedRooms) {
        this.allocatedRooms = allocatedRooms;
    }

    public ReservationRequest() {
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }


    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }


    public String getRoomUnit() {
        return roomUnit;
    }

    public void setRoomUnit(String roomUnit) {
        this.roomUnit = roomUnit;
    }


    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }



    public Integer getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(Integer guestCount) {
        this.guestCount = guestCount;
    }


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }


    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }


    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }


    public String getBookingSource() {
        return bookingSource;
    }

    public void setBookingSource(String bookingSource) {
        this.bookingSource = bookingSource;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
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