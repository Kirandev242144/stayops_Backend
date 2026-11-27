package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stayops_api.dto.StorefrontSettingsDTO;
import stayops_api.service.FileUploadService;
import stayops_api.service.StorefrontSettingsService;

import java.util.Map;

@Tag(name = "8. Public Storefront", description = "Public Storefront Theme & Customization Endpoint")
@RestController
@RequestMapping("/api/public/storefront-settings")
@CrossOrigin(origins = "*")
public class PublicStorefrontSettingsController {

    private final StorefrontSettingsService storefrontSettingsService;
    private final FileUploadService fileUploadService;

    public PublicStorefrontSettingsController(
            StorefrontSettingsService storefrontSettingsService,
            FileUploadService fileUploadService
    ) {
        this.storefrontSettingsService = storefrontSettingsService;
        this.fileUploadService = fileUploadService;
    }

    @Operation(summary = "Get Public Storefront Theme Customization by Subdomain or Property ID")
    @GetMapping("/{subdomainOrId}")
    public StorefrontSettingsDTO getPublicStorefrontSettings(
            @PathVariable String subdomainOrId
    ) {
        try {
            Long propertyId = Long.parseLong(subdomainOrId);
            return storefrontSettingsService.getPublicSettingsByProperty(propertyId);
        } catch (NumberFormatException e) {
            return storefrontSettingsService.getPublicSettingsBySubdomain(subdomainOrId);
        }
    }

    @Operation(summary = "Upload Storefront Customizer Section Image (Hero, About, etc.)")
    @PostMapping(value = "/upload-image", consumes = "multipart/form-data")
    public Map<String, String> uploadStorefrontImage(
            @RequestParam("file") MultipartFile file
    ) {
        String fileUrl = fileUploadService.storeImageFile(file);
        return Map.of("url", fileUrl);
    }
}
