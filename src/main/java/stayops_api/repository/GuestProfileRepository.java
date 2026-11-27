package stayops_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stayops_api.entity.GuestProfile;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuestProfileRepository extends JpaRepository<GuestProfile, Long> {

    Optional<GuestProfile> findByPhoneAndUserId(String phone, Long userId);

    List<GuestProfile> findByUserId(Long userId);

}
