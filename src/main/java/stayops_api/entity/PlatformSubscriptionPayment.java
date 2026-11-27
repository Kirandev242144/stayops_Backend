package stayops_api.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "platform_subscription_payments")
public class PlatformSubscriptionPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User tenant;

    @Column(name = "invoice_number", unique = true, nullable = false, length = 50)
    private String invoiceNumber;

    @Column(name = "plan_key", nullable = false, length = 50)
    private String planKey;

    @Column(name = "plan_name", nullable = false, length = 100)
    private String planName;

    @Column(name = "billing_cycle", nullable = false, length = 20)
    private String billingCycle = "monthly"; // monthly | yearly

    @Column(name = "subtotal_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotalAmount;

    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(length = 10)
    private String currency = "INR";

    @Column(name = "payment_gateway", length = 50)
    private String paymentGateway = "RAZORPAY";

    @Column(name = "razorpay_order_id", length = 100)
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id", length = 100)
    private String razorpayPaymentId;

    @Column(name = "razorpay_signature", length = 255)
    private String razorpaySignature;

    @Column(length = 30)
    private String status = "PAID"; // PAID | PENDING | FAILED | REFUNDED

    @Column(name = "payment_method", length = 50)
    private String paymentMethod = "Razorpay Online";

    @Column(name = "billing_name", length = 150)
    private String billingName;

    @Column(name = "billing_email", length = 150)
    private String billingEmail;

    @Column(name = "billing_gstin", length = 30)
    private String billingGstin;

    @Column(name = "period_start")
    private LocalDateTime periodStart;

    @Column(name = "period_end")
    private LocalDateTime periodEnd;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public PlatformSubscriptionPayment() {
    }

    public PlatformSubscriptionPayment(
            User tenant,
            String invoiceNumber,
            String planKey,
            String planName,
            String billingCycle,
            BigDecimal subtotalAmount,
            BigDecimal taxAmount,
            BigDecimal totalAmount,
            String razorpayOrderId,
            String razorpayPaymentId,
            String paymentMethod,
            String billingName,
            String billingEmail,
            String billingGstin,
            LocalDateTime periodStart,
            LocalDateTime periodEnd
    ) {
        this.tenant = tenant;
        this.invoiceNumber = invoiceNumber;
        this.planKey = planKey;
        this.planName = planName;
        this.billingCycle = billingCycle;
        this.subtotalAmount = subtotalAmount;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
        this.razorpayOrderId = razorpayOrderId;
        this.razorpayPaymentId = razorpayPaymentId;
        this.paymentMethod = paymentMethod;
        this.billingName = billingName;
        this.billingEmail = billingEmail;
        this.billingGstin = billingGstin;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.status = "PAID";
        this.currency = "INR";
        this.paymentGateway = "RAZORPAY";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getTenant() { return tenant; }
    public void setTenant(User tenant) { this.tenant = tenant; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getPlanKey() { return planKey; }
    public void setPlanKey(String planKey) { this.planKey = planKey; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public String getBillingCycle() { return billingCycle; }
    public void setBillingCycle(String billingCycle) { this.billingCycle = billingCycle; }

    public BigDecimal getSubtotalAmount() { return subtotalAmount; }
    public void setSubtotalAmount(BigDecimal subtotalAmount) { this.subtotalAmount = subtotalAmount; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPaymentGateway() { return paymentGateway; }
    public void setPaymentGateway(String paymentGateway) { this.paymentGateway = paymentGateway; }

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }

    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }

    public String getRazorpaySignature() { return razorpaySignature; }
    public void setRazorpaySignature(String razorpaySignature) { this.razorpaySignature = razorpaySignature; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getBillingName() { return billingName; }
    public void setBillingName(String billingName) { this.billingName = billingName; }

    public String getBillingEmail() { return billingEmail; }
    public void setBillingEmail(String billingEmail) { this.billingEmail = billingEmail; }

    public String getBillingGstin() { return billingGstin; }
    public void setBillingGstin(String billingGstin) { this.billingGstin = billingGstin; }

    public LocalDateTime getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDateTime periodStart) { this.periodStart = periodStart; }

    public LocalDateTime getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDateTime periodEnd) { this.periodEnd = periodEnd; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
