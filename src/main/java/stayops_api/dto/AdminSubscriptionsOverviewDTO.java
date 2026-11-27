package stayops_api.dto;

import java.util.List;
import java.util.Map;

public class AdminSubscriptionsOverviewDTO {

    private String mrrFormatted;
    private String arrFormatted;
    private long paidAccountsCount;
    private long trialAccountsCount;
    private String grossRevenueFormatted;
    private Map<String, Object> gatewayStatus;
    private Map<String, Long> planBreakdown;
    private List<PlatformSubscriptionDTO> transactions;
    private List<Map<String, Object>> plans;
    private List<Map<String, Object>> tenantsQuota;

    public AdminSubscriptionsOverviewDTO() {}

    public String getMrrFormatted() { return mrrFormatted; }
    public void setMrrFormatted(String mrrFormatted) { this.mrrFormatted = mrrFormatted; }

    public String getArrFormatted() { return arrFormatted; }
    public void setArrFormatted(String arrFormatted) { this.arrFormatted = arrFormatted; }

    public long getPaidAccountsCount() { return paidAccountsCount; }
    public void setPaidAccountsCount(long paidAccountsCount) { this.paidAccountsCount = paidAccountsCount; }

    public long getTrialAccountsCount() { return trialAccountsCount; }
    public void setTrialAccountsCount(long trialAccountsCount) { this.trialAccountsCount = trialAccountsCount; }

    public String getGrossRevenueFormatted() { return grossRevenueFormatted; }
    public void setGrossRevenueFormatted(String grossRevenueFormatted) { this.grossRevenueFormatted = grossRevenueFormatted; }

    public Map<String, Object> getGatewayStatus() { return gatewayStatus; }
    public void setGatewayStatus(Map<String, Object> gatewayStatus) { this.gatewayStatus = gatewayStatus; }

    public Map<String, Long> getPlanBreakdown() { return planBreakdown; }
    public void setPlanBreakdown(Map<String, Long> planBreakdown) { this.planBreakdown = planBreakdown; }

    public List<PlatformSubscriptionDTO> getTransactions() { return transactions; }
    public void setTransactions(List<PlatformSubscriptionDTO> transactions) { this.transactions = transactions; }

    public List<Map<String, Object>> getPlans() { return plans; }
    public void setPlans(List<Map<String, Object>> plans) { this.plans = plans; }

    public List<Map<String, Object>> getTenantsQuota() { return tenantsQuota; }
    public void setTenantsQuota(List<Map<String, Object>> tenantsQuota) { this.tenantsQuota = tenantsQuota; }
}
