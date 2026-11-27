package stayops_api.dto;

public class ConvertLeadRequest {

    private boolean createTenant = true;
    private Long existingTenantId;
    private String plan;
    private String note;

    public ConvertLeadRequest() {
    }

    public boolean isCreateTenant() { return createTenant; }
    public void setCreateTenant(boolean createTenant) { this.createTenant = createTenant; }

    public Long getExistingTenantId() { return existingTenantId; }
    public void setExistingTenantId(Long existingTenantId) { this.existingTenantId = existingTenantId; }

    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
