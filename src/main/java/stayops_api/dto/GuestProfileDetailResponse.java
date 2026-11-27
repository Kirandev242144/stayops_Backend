package stayops_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class GuestProfileDetailResponse {

    private GuestProfileResponse profile;
    private Integer totalStays;
    private BigDecimal totalSpend;
    private LocalDate lastStayDate;
    private List<ReservationResponse> reservations;

    public GuestProfileDetailResponse() {
    }

    public GuestProfileDetailResponse(
            GuestProfileResponse profile,
            Integer totalStays,
            BigDecimal totalSpend,
            LocalDate lastStayDate,
            List<ReservationResponse> reservations
    ) {
        this.profile = profile;
        this.totalStays = totalStays;
        this.totalSpend = totalSpend;
        this.lastStayDate = lastStayDate;
        this.reservations = reservations;
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

    public List<ReservationResponse> getReservations() {
        return reservations;
    }
}
