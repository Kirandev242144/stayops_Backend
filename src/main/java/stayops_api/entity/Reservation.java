package stayops_api.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Column(name = "guest_name", nullable = false)
    private String guestName;

    @Column(name = "guest_email")
    private String guestEmail;

    @Column(name = "guest_phone")
    private String guestPhone;


    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;


    @Column(name = "booking_ref", nullable = false, unique = true)
    private String bookingRef;


    @Column(name = "room_unit")
    private String roomUnit;


    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @Column(name = "check_in_time")
    private String checkInTime = "14:00";

    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    @Column(name = "check_out_time")
    private String checkOutTime = "11:00";



    @Column(name = "guest_count", nullable = false)
    private Integer guestCount = 1;


    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;


    @Column(name = "payment_status", nullable = false)
    private String paymentStatus = "Pending";


    @Column(name = "amount_paid", nullable = false)
    private BigDecimal amountPaid = BigDecimal.ZERO;


    @Column(name = "booking_source")
    private String bookingSource = "Walk-In";


    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;

    @Column(name = "room_base_total")
    private BigDecimal roomBaseTotal;

    @Column(name = "room_subtotal")
    private BigDecimal roomSubtotal;

    @Column(name = "room_service_charge")
    private BigDecimal roomServiceCharge;

    @Column(name = "room_gst_amount")
    private BigDecimal roomGstAmount;

    @Column(name = "tax_settings_snapshot", columnDefinition = "TEXT")
    private String taxSettingsSnapshot;

    @Column(name = "id_type")
    private String idType;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "id_document_url", columnDefinition = "LONGTEXT")
    private String idDocumentUrl;

    @Column(name = "id_documents", columnDefinition = "LONGTEXT")
    private String idDocuments;

    @Column(name = "guest_city")
    private String guestCity;

    @Column(name = "guest_state")
    private String guestState;

    @Column(name = "guest_telegram_chat_id")
    private String guestTelegramChatId;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private java.util.List<ReservationRoom> allocatedRooms = new java.util.ArrayList<>();

    @Column(nullable = false)
    private String status = "Confirmed";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public java.util.List<ReservationRoom> getAllocatedRooms() {
        return allocatedRooms;
    }

    public void setAllocatedRooms(java.util.List<ReservationRoom> allocatedRooms) {
        this.allocatedRooms = allocatedRooms;
    }

    public void addAllocatedRoom(ReservationRoom room) {
        if (room != null) {
            room.setReservation(this);
            this.allocatedRooms.add(room);
        }
    }


    public Reservation() {
    }


    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }


    public Long getId() {
        return id;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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


    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }


    public String getBookingRef() {
        return bookingRef;
    }

    public void setBookingRef(String bookingRef) {
        this.bookingRef = bookingRef;
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

    public String getGuestTelegramChatId() {
        return guestTelegramChatId;
    }

    public void setGuestTelegramChatId(String guestTelegramChatId) {
        this.guestTelegramChatId = guestTelegramChatId;
    }
}