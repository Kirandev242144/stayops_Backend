package stayops_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stayops_api.entity.MealOrder;

import java.util.List;

@Repository
public interface MealOrderRepository extends JpaRepository<MealOrder, Long> {
    List<MealOrder> findByUserId(Long userId);
    List<MealOrder> findByReservationIdAndUserId(Long reservationId, Long userId);
}
