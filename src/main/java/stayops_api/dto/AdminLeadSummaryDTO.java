package stayops_api.dto;

public class AdminLeadSummaryDTO {

    private long totalLeads;
    private long newLeads;
    private long demosScheduled;
    private long followUpsDue;
    private long convertedLeads;

    public AdminLeadSummaryDTO() {
    }

    public AdminLeadSummaryDTO(long totalLeads, long newLeads, long demosScheduled, long followUpsDue, long convertedLeads) {
        this.totalLeads = totalLeads;
        this.newLeads = newLeads;
        this.demosScheduled = demosScheduled;
        this.followUpsDue = followUpsDue;
        this.convertedLeads = convertedLeads;
    }

    public long getTotalLeads() { return totalLeads; }
    public void setTotalLeads(long totalLeads) { this.totalLeads = totalLeads; }

    public long getNewLeads() { return newLeads; }
    public void setNewLeads(long newLeads) { this.newLeads = newLeads; }

    public long getDemosScheduled() { return demosScheduled; }
    public void setDemosScheduled(long demosScheduled) { this.demosScheduled = demosScheduled; }

    public long getFollowUpsDue() { return followUpsDue; }
    public void setFollowUpsDue(long followUpsDue) { this.followUpsDue = followUpsDue; }

    public long getConvertedLeads() { return convertedLeads; }
    public void setConvertedLeads(long convertedLeads) { this.convertedLeads = convertedLeads; }
}
