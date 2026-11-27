package stayops_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stayops_api.entity.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUser_Id(Long userId);
    List<Reservation> findByProperty_Id(Long propertyId);
    List<Reservation> findByGuestPhoneAndUser_IdOrderByCheckInDesc(String guestPhone, Long userId);
    boolean existsByBookingRef(String bookingRef);
    Optional<Reservation> findByBookingRef(String bookingRef);
}