package stayops_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stayops_api.entity.TeamMember;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByOwnerId(Long ownerId);

    Optional<TeamMember> findByMobileNumber(String mobileNumber);

    boolean existsByMobileNumber(String mobileNumber);

    boolean existsByMobileNumberAndIdNot(String mobileNumber, Long id);
}
