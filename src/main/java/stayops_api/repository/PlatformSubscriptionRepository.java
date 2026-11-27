package stayops_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import stayops_api.entity.PlatformSubscriptionPayment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PlatformSubscriptionRepository extends JpaRepository<PlatformSubscriptionPayment, Long> {

    List<PlatformSubscriptionPayment> findAllByOrderByCreatedAtDesc();

    List<PlatformSubscriptionPayment> findByTenant_IdOrderByCreatedAtDesc(Long tenantId);

    Optional<PlatformSubscriptionPayment> findByInvoiceNumber(String invoiceNumber);

    Optional<PlatformSubscriptionPayment> findByRazorpayOrderId(String razorpayOrderId);

    long countByStatus(String status);

    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM PlatformSubscriptionPayment p WHERE p.status = 'PAID'")
    BigDecimal calculateTotalGrossRevenue();
}
