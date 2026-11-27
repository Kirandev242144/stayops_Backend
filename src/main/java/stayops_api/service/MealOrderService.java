package stayops_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stayops_api.entity.MealOrder;
import stayops_api.entity.User;
import stayops_api.repository.MealOrderRepository;

import java.util.List;

@Service
public class MealOrderService {

    @Autowired
    private MealOrderRepository mealOrderRepository;

    public MealOrder saveMealOrder(MealOrder mealOrder, Long userId) {
        User user = new User();
        user.setId(userId);
        mealOrder.setUser(user);
        return mealOrderRepository.save(mealOrder);
    }

    public List<MealOrder> getAllMealOrders(Long userId) {
        return mealOrderRepository.findByUserId(userId);
    }

    public List<MealOrder> getMealOrdersByReservation(Long reservationId, Long userId) {
        return mealOrderRepository.findByReservationIdAndUserId(reservationId, userId);
    }
}
