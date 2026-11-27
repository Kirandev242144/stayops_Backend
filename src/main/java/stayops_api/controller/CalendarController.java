package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import stayops_api.entity.DailyRateOverride;
import stayops_api.entity.RoomBlockedDate;
import stayops_api.repository.DailyRateOverrideRepository;
import stayops_api.repository.RoomBlockedDateRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "4. Calendar & Rate Ops", description = "Room Blackout Dates, Maintenance, and Seasonal Rate Overrides")
@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final RoomBlockedDateRepository blockedDateRepository;
    private final DailyRateOverrideRepository rateOverrideRepository;

    public CalendarController(
            RoomBlockedDateRepository blockedDateRepository,
            DailyRateOverrideRepository rateOverrideRepository
    ) {
        this.blockedDateRepository = blockedDateRepository;
        this.rateOverrideRepository = rateOverrideRepository;
    }

    // =========================================================
    // BLOCKED DATES (MAINTENANCE / OOO)
    // =========================================================

    @Operation(summary = "Get all blocked date ranges")
    @GetMapping("/blocked-dates")
    public List<RoomBlockedDate> getAllBlockedDates(
            @RequestParam(required = false) Long propertyId
    ) {
        if (propertyId != null) {
            return blockedDateRepository.findByPropertyId(propertyId);
        }
        return blockedDateRepository.findAll();
    }

    @Operation(summary = "Create a new blocked date range")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Blocked date range payload",
        content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                name = "Blocked Date Example",
                value = "{\n  \"propertyId\": 33,\n  \"roomId\": \"Deluxe Villa\",\n  \"startDate\": \"2026-10-01\",\n  \"endDate\": \"2026-10-05\",\n  \"category\": \"Maintenance\",\n  \"notes\": \"Air conditioning repair & painting\"\n}"
            )
        )
    )
    @PostMapping("/blocked-dates")
    @ResponseStatus(HttpStatus.CREATED)
    public RoomBlockedDate createBlockedDate(@RequestBody RoomBlockedDate block) {
        if (block.getPropertyId() == null) {
            block.setPropertyId(1L);
        }
        if (block.getRoomId() == null) {
            block.setRoomId("all");
        }
        return blockedDateRepository.save(block);
    }

    @Operation(summary = "Delete a blocked date range")
    @DeleteMapping("/blocked-dates/{id}")
    public Map<String, String> deleteBlockedDate(@PathVariable Long id) {
        blockedDateRepository.deleteById(id);
        return Map.of("message", "Blocked date range deleted successfully");
    }

    // =========================================================
    // DAILY RATE OVERRIDES
    // =========================================================

    @Operation(summary = "Get all custom rate overrides")
    @GetMapping("/rate-overrides")
    public List<DailyRateOverride> getAllRateOverrides(
            @RequestParam(required = false) Long propertyId
    ) {
        if (propertyId != null) {
            return rateOverrideRepository.findByPropertyId(propertyId);
        }
        return rateOverrideRepository.findAll();
    }

    @Operation(summary = "Save or update a single daily rate override")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Daily rate override request payload",
        content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                name = "Rate Override Example",
                value = "{\n  \"propertyId\": 33,\n  \"roomId\": \"Deluxe Villa\",\n  \"date\": \"2026-10-15\",\n  \"overrideRate\": 4500.00\n}"
            )
        )
    )
    @PostMapping("/rate-overrides")
    public DailyRateOverride saveSingleRateOverride(@RequestBody Map<String, Object> payload) {
        Long propertyId = payload.get("propertyId") != null ? Long.valueOf(payload.get("propertyId").toString()) : 1L;
        String roomId = payload.get("roomId") != null ? payload.get("roomId").toString() : "all";
        LocalDate date = LocalDate.parse(payload.get("date").toString());
        BigDecimal rate = new BigDecimal(payload.get("overrideRate").toString());

        Optional<DailyRateOverride> existing = rateOverrideRepository.findByPropertyIdAndRoomIdAndDate(propertyId, roomId, date);
        DailyRateOverride override = existing.orElseGet(DailyRateOverride::new);
        override.setPropertyId(propertyId);
        override.setRoomId(roomId);
        override.setDate(date);
        override.setOverrideRate(rate);

        return rateOverrideRepository.save(override);
    }

    @Operation(summary = "Apply bulk rate updates across a date range")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Bulk rate overrides array payload",
        content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                name = "Bulk Rate Overrides Example",
                value = "[\n  {\n    \"propertyId\": 33,\n    \"roomId\": \"Deluxe Villa\",\n    \"date\": \"2026-10-15\",\n    \"overrideRate\": 4500.00\n  },\n  {\n    \"propertyId\": 33,\n    \"roomId\": \"Deluxe Villa\",\n    \"date\": \"2026-10-16\",\n    \"overrideRate\": 5200.00\n  }\n]"
            )
        )
    )
    @PostMapping("/rate-overrides/bulk")
    public Map<String, Object> applyBulkRateOverrides(@RequestBody Object payload) {
        int count = 0;
        if (payload instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) payload;
            for (Map<String, Object> item : items) {
                Long propertyId = item.get("propertyId") != null ? Long.valueOf(item.get("propertyId").toString()) : 1L;
                String roomId = item.get("roomId") != null ? item.get("roomId").toString() : "all";
                LocalDate date = LocalDate.parse(item.get("date").toString());
                BigDecimal rate = new BigDecimal(item.get("overrideRate").toString());

                Optional<DailyRateOverride> existing = rateOverrideRepository.findByPropertyIdAndRoomIdAndDate(propertyId, roomId, date);
                DailyRateOverride override = existing.orElseGet(DailyRateOverride::new);
                override.setPropertyId(propertyId);
                override.setRoomId(roomId);
                override.setDate(date);
                override.setOverrideRate(rate);

                rateOverrideRepository.save(override);
                count++;
            }
        } else if (payload instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> mapPayload = (Map<String, Object>) payload;
            if (mapPayload.containsKey("items") && mapPayload.get("items") instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> items = (List<Map<String, Object>>) mapPayload.get("items");
                for (Map<String, Object> item : items) {
                    Long propertyId = item.get("propertyId") != null ? Long.valueOf(item.get("propertyId").toString()) : 1L;
                    String roomId = item.get("roomId") != null ? item.get("roomId").toString() : "all";
                    LocalDate date = LocalDate.parse(item.get("date").toString());
                    BigDecimal rate = new BigDecimal(item.get("overrideRate").toString());

                    Optional<DailyRateOverride> existing = rateOverrideRepository.findByPropertyIdAndRoomIdAndDate(propertyId, roomId, date);
                    DailyRateOverride override = existing.orElseGet(DailyRateOverride::new);
                    override.setPropertyId(propertyId);
                    override.setRoomId(roomId);
                    override.setDate(date);
                    override.setOverrideRate(rate);

                    rateOverrideRepository.save(override);
                    count++;
                }
            } else if (mapPayload.containsKey("startDate") && mapPayload.containsKey("endDate")) {
                Long propertyId = mapPayload.get("propertyId") != null ? Long.valueOf(mapPayload.get("propertyId").toString()) : 1L;
                String roomId = mapPayload.get("roomId") != null ? mapPayload.get("roomId").toString() : "all";
                LocalDate startDate = LocalDate.parse(mapPayload.get("startDate").toString());
                LocalDate endDate = LocalDate.parse(mapPayload.get("endDate").toString());
                BigDecimal rateValue = new BigDecimal(mapPayload.get("rateValue").toString());

                LocalDate current = startDate;
                while (!current.isAfter(endDate)) {
                    Optional<DailyRateOverride> existing = rateOverrideRepository.findByPropertyIdAndRoomIdAndDate(propertyId, roomId, current);
                    DailyRateOverride override = existing.orElseGet(DailyRateOverride::new);
                    override.setPropertyId(propertyId);
                    override.setRoomId(roomId);
                    override.setDate(current);
                    override.setOverrideRate(rateValue);

                    rateOverrideRepository.save(override);
                    count++;
                    current = current.plusDays(1);
                }
            }
        }

        return Map.of("message", "Bulk rate overrides applied successfully", "daysUpdated", count);
    }
}
