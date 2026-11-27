package stayops_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stayops_api.entity.LeadNote;
import java.util.List;

public interface LeadNoteRepository extends JpaRepository<LeadNote, Long> {
    List<LeadNote> findByLeadIdOrderByCreatedAtDesc(Long leadId);
}
