package stayops_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stayops_api.entity.DailyRateOverride;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyRateOverrideRepository extends JpaRepository<DailyRateOverride, Long> {
    List<DailyRateOverride> findByPropertyId(Long propertyId);
    Optional<DailyRateOverride> findByPropertyIdAndRoomIdAndDate(Long propertyId, String roomId, LocalDate date);
    List<DailyRateOverride> findByPropertyIdAndDateBetween(Long propertyId, LocalDate startDate, LocalDate endDate);
}
