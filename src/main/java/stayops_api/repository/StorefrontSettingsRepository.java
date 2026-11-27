package stayops_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stayops_api.entity.StorefrontSettings;

import java.util.Optional;

public interface StorefrontSettingsRepository extends JpaRepository<StorefrontSettings, Long> {
    Optional<StorefrontSettings> findByPropertyId(Long propertyId);
    Optional<StorefrontSettings> findBySubdomain(String subdomain);
}
