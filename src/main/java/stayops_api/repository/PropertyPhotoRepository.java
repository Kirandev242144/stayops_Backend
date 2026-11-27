package stayops_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stayops_api.entity.PropertyPhoto;

import java.util.List;

public interface PropertyPhotoRepository extends JpaRepository<PropertyPhoto, Long> {
    List<PropertyPhoto> findByProperty_Id(Long propertyId);
    void deleteByProperty_Id(Long propertyId);
    void deleteByProperty_IdAndUrl(Long propertyId, String url);
}
