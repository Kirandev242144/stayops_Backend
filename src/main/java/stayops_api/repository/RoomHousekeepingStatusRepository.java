package stayops_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stayops_api.entity.RoomHousekeepingStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomHousekeepingStatusRepository extends JpaRepository<RoomHousekeepingStatus, Long> {
    List<RoomHousekeepingStatus> findByPropertyId(Long propertyId);
    Optional<RoomHousekeepingStatus> findByPropertyIdAndRoomId(Long propertyId, String roomId);
}
