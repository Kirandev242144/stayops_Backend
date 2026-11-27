package stayops_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stayops_api.entity.PettyCashAccount;

import java.util.Optional;

@Repository
public interface PettyCashAccountRepository extends JpaRepository<PettyCashAccount, Long> {
    Optional<PettyCashAccount> findByPropertyId(Long propertyId);
}
