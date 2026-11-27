package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import stayops_api.entity.MealOrder;
import stayops_api.service.MealOrderService;

import java.util.List;

@Tag(name = "5. Meal Orders & Dining", description = "F&B Meal Order logging against active stays")
@RestController
@RequestMapping("/api/meal-orders")
@CrossOrigin(origins = "*")
public class MealOrderController {

    @Autowired
    private MealOrderService mealOrderService;

    @Operation(summary = "Log a new Meal Order for a Guest Stay")
    @PostMapping
    public ResponseEntity<MealOrder> createMealOrder(
            @RequestBody MealOrder mealOrder,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        MealOrder saved = mealOrderService.saveMealOrder(mealOrder, userId);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<MealOrder>> getAllMealOrders(Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        List<MealOrder> orders = mealOrderService.getAllMealOrders(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/reservation/{resId}")
    public ResponseEntity<List<MealOrder>> getMealOrdersByReservation(
            @PathVariable Long resId,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        List<MealOrder> orders = mealOrderService.getMealOrdersByReservation(resId, userId);
        return ResponseEntity.ok(orders);
    }

    private Long getAuthenticatedUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            throw new RuntimeException("User is not authenticated");
        }
        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid authenticated user ID");
        }
    }
}
