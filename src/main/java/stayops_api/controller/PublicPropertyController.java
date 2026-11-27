package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import stayops_api.dto.PropertyResponse;
import stayops_api.dto.TaxSettingsDTO;
import stayops_api.service.PropertyService;
import stayops_api.service.TaxSettingsService;

@Tag(name = "8. Public Storefront", description = "Public Property Details, Availability, and Storefront Policies")
@RestController
@RequestMapping("/api/public/properties")
@CrossOrigin(origins = "*")
public class PublicPropertyController {

    private final PropertyService propertyService;
    private final TaxSettingsService taxSettingsService;

    public PublicPropertyController(PropertyService propertyService, TaxSettingsService taxSettingsService) {
        this.propertyService = propertyService;
        this.taxSettingsService = taxSettingsService;
    }

    @Operation(summary = "Get Public Property Details by ID or Subdomain Slug")
    @GetMapping("/{slugOrId}")
    public PropertyResponse getPublicProperty(@PathVariable String slugOrId) {
        return propertyService.getPublicPropertyBySlugOrId(slugOrId);
    }

    @Operation(summary = "Get Public Property Tax & Service Charge Settings")
    @GetMapping("/{slugOrId}/taxes")
    public TaxSettingsDTO getPublicTaxSettings(@PathVariable String slugOrId) {
        PropertyResponse prop = propertyService.getPublicPropertyBySlugOrId(slugOrId);
        return taxSettingsService.getTaxSettingsByProperty(prop.getId(), null);
    }
}
