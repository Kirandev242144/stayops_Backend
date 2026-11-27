package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import stayops_api.dto.PropertyPolicyDTO;
import stayops_api.service.PropertyPolicyService;

@Tag(name = "8. Public Storefront", description = "Public Property Details, Availability, and Storefront Policies")
@RestController
@RequestMapping("/api/public/properties/{propertyId}/policies")
@CrossOrigin(origins = "http://localhost:5173")
public class PublicPropertyPolicyController {

    private final PropertyPolicyService propertyPolicyService;

    public PublicPropertyPolicyController(PropertyPolicyService propertyPolicyService) {
        this.propertyPolicyService = propertyPolicyService;
    }

    @Operation(summary = "Get Public Property Cancellation Policy for Storefront Guests")
    @GetMapping
    public PropertyPolicyDTO getPublicPolicies(
            @PathVariable Long propertyId
    ) {
        return propertyPolicyService.getPublicPolicyByProperty(propertyId);
    }
}
