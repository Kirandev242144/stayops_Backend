package stayops_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stayops_api.entity.PaymentGatewayCredentials;

import java.util.Optional;

@Repository
public interface PaymentGatewayCredentialsRepository extends JpaRepository<PaymentGatewayCredentials, Long> {

    Optional<PaymentGatewayCredentials> findByUserIdAndGatewayName(Long userId, String gatewayName);

    Optional<PaymentGatewayCredentials> findByUserId(Long userId);
}
