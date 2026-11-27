package stayops_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stayops_api.entity.SalesLead;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SalesLeadRepository extends JpaRepository<SalesLead, Long>, JpaSpecificationExecutor<SalesLead> {

    Optional<SalesLead> findByNormalizedEmail(String normalizedEmail);

    List<SalesLead> findByNormalizedEmailOrNormalizedPhone(String normalizedEmail, String normalizedPhone);

    // Summary counts
    long countByStatus(String status);

    @Query("SELECT COUNT(l) FROM SalesLead l WHERE l.nextFollowUpAt <= :now AND l.status <> 'CONVERTED' AND l.status <> 'NOT_INTERESTED'")
    long countFollowUpsDue(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(l) FROM SalesLead l WHERE l.demoScheduledAt IS NOT NULL AND l.status = 'DEMO_SCHEDULED'")
    long countDemosScheduled();

    // Query for spam/duplicate detection within the last N minutes
    @Query("SELECT l FROM SalesLead l WHERE l.normalizedEmail = :email AND l.createdAt >= :since")
    List<SalesLead> findRecentByEmail(@Param("email") String email, @Param("since") LocalDateTime since);
}
