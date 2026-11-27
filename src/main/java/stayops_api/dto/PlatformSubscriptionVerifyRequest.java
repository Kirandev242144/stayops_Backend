package stayops_api.dto;

public class PlatformSubscriptionVerifyRequest {

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
    private String planKey;
    private String billingCycle;
    private Long tenantId;
    private String billingName;
    private String billingEmail;
    private String billingGstin;

    public PlatformSubscriptionVerifyRequest() {}

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }

    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }

    public String getRazorpaySignature() { return razorpaySignature; }
    public void setRazorpaySignature(String razorpaySignature) { this.razorpaySignature = razorpaySignature; }

    public String getPlanKey() { return planKey; }
    public void setPlanKey(String planKey) { this.planKey = planKey; }

    public String getBillingCycle() { return billingCycle; }
    public void setBillingCycle(String billingCycle) { this.billingCycle = billingCycle; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public String getBillingName() { return billingName; }
    public void setBillingName(String billingName) { this.billingName = billingName; }

    public String getBillingEmail() { return billingEmail; }
    public void setBillingEmail(String billingEmail) { this.billingEmail = billingEmail; }

    public String getBillingGstin() { return billingGstin; }
    public void setBillingGstin(String billingGstin) { this.billingGstin = billingGstin; }
}
