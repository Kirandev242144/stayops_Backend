package stayops_api.dto;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import stayops_api.entity.PlatformSubscriptionPayment;

public class PlatformSubscriptionDTO {

    private Long id;
    private String invoiceNumber;
    private Long tenantId;
    private String tenantName;
    private String tenantEmail;
    private String propertyName;
    private String planKey;
    private String planName;
    private String billingCycle;
    private BigDecimal subtotalAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String formattedAmount;
    private String currency;
    private String paymentGateway;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String status;
    private String paymentMethod;
    private String billingGstin;
    private String date;
    private String periodStart;
    private String periodEnd;

    public PlatformSubscriptionDTO() {}

    public static PlatformSubscriptionDTO fromEntity(PlatformSubscriptionPayment p, String propertyName) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
        PlatformSubscriptionDTO dto = new PlatformSubscriptionDTO();
        dto.setId(p.getId());
        dto.setInvoiceNumber(p.getInvoiceNumber());
        if (p.getTenant() != null) {
            dto.setTenantId(p.getTenant().getId());
            dto.setTenantName(p.getBillingName() != null && !p.getBillingName().isBlank()
                    ? p.getBillingName() : p.getTenant().getName());
            dto.setTenantEmail(p.getBillingEmail() != null && !p.getBillingEmail().isBlank()
                    ? p.getBillingEmail() : p.getTenant().getEmail());
        } else {
            dto.setTenantName(p.getBillingName() != null ? p.getBillingName() : "Owner");
            dto.setTenantEmail(p.getBillingEmail() != null ? p.getBillingEmail() : "—");
        }
        dto.setPropertyName(propertyName != null ? propertyName : "StayOps Registered Stay");
        dto.setPlanKey(p.getPlanKey());
        dto.setPlanName(p.getPlanName());
        dto.setBillingCycle(p.getBillingCycle());
        dto.setSubtotalAmount(p.getSubtotalAmount());
        dto.setTaxAmount(p.getTaxAmount());
        dto.setTotalAmount(p.getTotalAmount());
        dto.setFormattedAmount("₹" + (p.getTotalAmount() != null ? p.getTotalAmount().toPlainString() : "0"));
        dto.setCurrency(p.getCurrency());
        dto.setPaymentGateway(p.getPaymentGateway());
        dto.setRazorpayOrderId(p.getRazorpayOrderId());
        dto.setRazorpayPaymentId(p.getRazorpayPaymentId());
        dto.setStatus(p.getStatus());
        dto.setPaymentMethod(p.getPaymentMethod());
        dto.setBillingGstin(p.getBillingGstin());
        dto.setDate(p.getCreatedAt() != null ? p.getCreatedAt().format(fmt) : "Recent");
        dto.setPeriodStart(p.getPeriodStart() != null ? p.getPeriodStart().format(fmt) : null);
        dto.setPeriodEnd(p.getPeriodEnd() != null ? p.getPeriodEnd().format(fmt) : null);
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }

    public String getTenantEmail() { return tenantEmail; }
    public void setTenantEmail(String tenantEmail) { this.tenantEmail = tenantEmail; }

    public String getPropertyName() { return propertyName; }
    public void setPropertyName(String propertyName) { this.propertyName = propertyName; }

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

    public String getFormattedAmount() { return formattedAmount; }
    public void setFormattedAmount(String formattedAmount) { this.formattedAmount = formattedAmount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPaymentGateway() { return paymentGateway; }
    public void setPaymentGateway(String paymentGateway) { this.paymentGateway = paymentGateway; }

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }

    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getBillingGstin() { return billingGstin; }
    public void setBillingGstin(String billingGstin) { this.billingGstin = billingGstin; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getPeriodStart() { return periodStart; }
    public void setPeriodStart(String periodStart) { this.periodStart = periodStart; }

    public String getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(String periodEnd) { this.periodEnd = periodEnd; }
}
