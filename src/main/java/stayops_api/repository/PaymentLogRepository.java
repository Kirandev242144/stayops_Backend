package stayops_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stayops_api.entity.PaymentLog;

import java.util.List;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {

    List<PaymentLog> findByUserIdOrderByCreatedAtDesc(Long userId);

}
