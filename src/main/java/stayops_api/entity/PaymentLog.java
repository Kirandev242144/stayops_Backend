package stayops_api.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_logs")
public class PaymentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "booking_ref", length = 50)
    private String bookingRef;

    @Column(name = "guest_name", length = 100)
    private String guestName;

    @Column(name = "guest_phone", length = 30)
    private String guestPhone;

    @Column(name = "type", length = 20, nullable = false)
    private String type = "PAYMENT"; // PAYMENT, REFUND

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "payment_gateway", length = 50)
    private String paymentGateway = "MANUAL"; // RAZORPAY, CASHFREE, MANUAL

    @Column(name = "payment_method", length = 100)
    private String paymentMethod;

    @Column(name = "refund_method", length = 100)
    private String refundMethod;

    @Column(name = "order_id", length = 100)
    private String orderId;

    @Column(name = "payment_id", length = 100)
    private String paymentId;

    @Column(name = "refund_id", length = 100)
    private String refundId;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @Column(name = "status", length = 30, nullable = false)
    private String status = "CAPTURED"; // CAPTURED, REFUNDED, PENDING, FAILED

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public PaymentLog() {
    }

    public PaymentLog(Long userId, String bookingRef, String guestName, String guestPhone, String type, BigDecimal amount, String paymentGateway, String paymentMethod, String orderId, String paymentId, String status, String notes) {
        this.userId = userId;
        this.bookingRef = bookingRef;
        this.guestName = guestName;
        this.guestPhone = guestPhone;
        this.type = type;
        this.amount = amount;
        this.paymentGateway = paymentGateway;
        this.paymentMethod = paymentMethod;
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.status = status;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getBookingRef() {
        return bookingRef;
    }

    public void setBookingRef(String bookingRef) {
        this.bookingRef = bookingRef;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(String paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getRefundMethod() {
        return refundMethod;
    }

    public void setRefundMethod(String refundMethod) {
        this.refundMethod = refundMethod;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
