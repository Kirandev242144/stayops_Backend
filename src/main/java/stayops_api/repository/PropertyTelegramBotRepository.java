package stayops_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stayops_api.entity.PropertyTelegramBot;

import java.util.Optional;

@Repository
public interface PropertyTelegramBotRepository extends JpaRepository<PropertyTelegramBot, Long> {

    Optional<PropertyTelegramBot> findByPropertyId(Long propertyId);

    Optional<PropertyTelegramBot> findByBotUsername(String botUsername);
}
