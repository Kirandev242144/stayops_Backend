package stayops_api.dto;

public class PlatformSubscriptionOrderRequest {

    private String planKey;       // lite | pro | hub
    private String billingCycle;  // monthly | yearly
    private Long tenantId;        // Optional if authenticated
    private String billingName;
    private String billingEmail;
    private String billingGstin;

    public PlatformSubscriptionOrderRequest() {}

    public String getPlanKey() { return planKey; }
    public void setPlanKey(String planKey) { this.planKey = planKey; }

    public String getBillingCycle() { return billingCycle != null ? billingCycle : "monthly"; }
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
