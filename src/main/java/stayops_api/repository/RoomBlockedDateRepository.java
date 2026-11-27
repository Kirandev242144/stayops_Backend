package stayops_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stayops_api.entity.RoomBlockedDate;
import java.util.List;

public interface RoomBlockedDateRepository extends JpaRepository<RoomBlockedDate, Long> {
    List<RoomBlockedDate> findByPropertyId(Long propertyId);
    List<RoomBlockedDate> findByPropertyIdAndRoomId(Long propertyId, String roomId);
}
