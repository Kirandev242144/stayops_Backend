package stayops_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stayops_api.entity.TaxSettings;

import java.util.Optional;

public interface TaxSettingsRepository extends JpaRepository<TaxSettings, Long> {
    Optional<TaxSettings> findByPropertyId(Long propertyId);
}
