package stayops_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stayops_api.entity.PettyCashTransaction;

import java.util.List;

@Repository
public interface PettyCashTransactionRepository extends JpaRepository<PettyCashTransaction, Long> {
    List<PettyCashTransaction> findByPropertyIdOrderByCreatedAtDesc(Long propertyId);
    List<PettyCashTransaction> findAllByOrderByCreatedAtDesc();
}
