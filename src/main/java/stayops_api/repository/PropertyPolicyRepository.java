package stayops_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stayops_api.entity.PropertyPolicy;

import java.util.Optional;

public interface PropertyPolicyRepository extends JpaRepository<PropertyPolicy, Long> {
    Optional<PropertyPolicy> findByPropertyId(Long propertyId);
}
