package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import stayops_api.dto.StorefrontSettingsDTO;
import stayops_api.service.StorefrontSettingsService;

@Tag(name = "8. Public Storefront", description = "Storefront Studio Theme & Customization Settings")
@RestController
@RequestMapping("/api/properties/{propertyId}/storefront-settings")
@CrossOrigin(origins = "http://localhost:5173")
public class StorefrontSettingsController {

    private final StorefrontSettingsService storefrontSettingsService;

    public StorefrontSettingsController(StorefrontSettingsService storefrontSettingsService) {
        this.storefrontSettingsService = storefrontSettingsService;
    }

    @Operation(summary = "Get Storefront Studio Customizer settings for a property (Owner)")
    @GetMapping
    public StorefrontSettingsDTO getSettings(
            @PathVariable Long propertyId,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        return storefrontSettingsService.getSettingsByProperty(propertyId, userId);
    }

    @Operation(summary = "Save or Update Storefront Studio Customizer settings for a property (Owner)")
    @PutMapping
    public StorefrontSettingsDTO updateSettings(
            @PathVariable Long propertyId,
            @RequestBody StorefrontSettingsDTO dto,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        return storefrontSettingsService.saveSettingsByProperty(propertyId, userId, dto);
    }

    @PostMapping
    public StorefrontSettingsDTO saveSettings(
            @PathVariable Long propertyId,
            @RequestBody StorefrontSettingsDTO dto,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        return storefrontSettingsService.saveSettingsByProperty(propertyId, userId, dto);
    }

    @Operation(summary = "Upload Storefront Customizer Section Image (Owner)")
    @PostMapping(value = "/upload-image", consumes = "multipart/form-data")
    public java.util.Map<String, String> uploadStorefrontImage(
            @PathVariable Long propertyId,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        String fileUrl = storefrontSettingsService.uploadImageForProperty(propertyId, userId, file);
        return java.util.Map.of("url", fileUrl);
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
