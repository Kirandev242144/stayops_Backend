package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import stayops_api.dto.TaxSettingsDTO;
import stayops_api.service.TaxSettingsService;

@Tag(name = "7. Tax & Policy Settings", description = "GST Tax Modes, Service Charges, and Cancellation Policies")
@RestController
@RequestMapping("/api/properties/{propertyId}/taxes")
@CrossOrigin(origins = "http://localhost:5173")
public class TaxSettingsController {

    private final TaxSettingsService taxSettingsService;

    public TaxSettingsController(TaxSettingsService taxSettingsService) {
        this.taxSettingsService = taxSettingsService;
    }

    @Operation(summary = "Get GST & Service Charge settings for a property")
    @GetMapping
    public TaxSettingsDTO getTaxSettings(
            @PathVariable Long propertyId,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        return taxSettingsService.getTaxSettingsByProperty(propertyId, userId);
    }

    @PostMapping
    public TaxSettingsDTO saveTaxSettings(
            @PathVariable Long propertyId,
            @RequestBody TaxSettingsDTO dto,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        return taxSettingsService.saveTaxSettingsByProperty(propertyId, userId, dto);
    }

    private Long getAuthenticatedUserId(Authentication authentication) {
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getName() == null) {
            throw new RuntimeException("User is not authenticated");
        }
        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid authenticated user ID");
        }
    }
}
