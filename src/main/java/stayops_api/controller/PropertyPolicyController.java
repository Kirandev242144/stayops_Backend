package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import stayops_api.dto.PropertyPolicyDTO;
import stayops_api.service.PropertyPolicyService;

@Tag(name = "7. Tax & Policy Settings", description = "GST Tax Modes, Service Charges, and Cancellation Policies")
@RestController
@RequestMapping("/api/properties/{propertyId}/policies")
@CrossOrigin(origins = "http://localhost:5173")
public class PropertyPolicyController {

    private final PropertyPolicyService propertyPolicyService;

    public PropertyPolicyController(PropertyPolicyService propertyPolicyService) {
        this.propertyPolicyService = propertyPolicyService;
    }

    @Operation(summary = "Get Check-in/Check-out & Cancellation rules for a property (Owner)")
    @GetMapping
    public PropertyPolicyDTO getPolicies(
            @PathVariable Long propertyId,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        return propertyPolicyService.getPolicyByProperty(propertyId, userId);
    }

    @PostMapping
    public PropertyPolicyDTO savePolicies(
            @PathVariable Long propertyId,
            @RequestBody PropertyPolicyDTO dto,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        return propertyPolicyService.savePolicyByProperty(propertyId, userId, dto);
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
