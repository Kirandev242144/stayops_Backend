package stayops_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GuestCrmSummary {

    private GuestProfileResponse profile;
    private Integer totalStays;
    private BigDecimal totalSpend;
    private LocalDate lastStayDate;

    public GuestCrmSummary() {
    }

    public GuestCrmSummary(
            GuestProfileResponse profile,
            Integer totalStays,
            BigDecimal totalSpend,
            LocalDate lastStayDate
    ) {
        this.profile = profile;
        this.totalStays = totalStays;
        this.totalSpend = totalSpend;
        this.lastStayDate = lastStayDate;
    }

    public GuestProfileResponse getProfile() {
        return profile;
    }

    public Integer getTotalStays() {
        return totalStays;
    }

    public BigDecimal getTotalSpend() {
        return totalSpend;
    }

    public LocalDate getLastStayDate() {
        return lastStayDate;
    }
}
