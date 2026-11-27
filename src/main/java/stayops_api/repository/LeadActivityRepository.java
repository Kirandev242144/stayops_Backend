package stayops_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stayops_api.entity.LeadActivity;
import java.util.List;

public interface LeadActivityRepository extends JpaRepository<LeadActivity, Long> {
    List<LeadActivity> findByLeadIdOrderByCreatedAtDesc(Long leadId);
}
