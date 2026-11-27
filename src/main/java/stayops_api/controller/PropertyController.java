package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import stayops_api.dto.PropertyRequest;
import stayops_api.dto.PropertyResponse;
import stayops_api.service.PropertyService;

import java.util.List;

@Tag(name = "2. Property & Room Inventory", description = "Property setup, Room categories, Wi-Fi, Amenities, and Photos")
@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @Operation(summary = "Create a new Property with Basic Details & Address")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyResponse createProperty(
            @RequestBody PropertyRequest request,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        return propertyService.createProperty(request, userId);
    }

    @GetMapping
    public List<PropertyResponse> getMyProperties(Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        return propertyService.getMyProperties(userId);
    }

    @GetMapping("/{id}")
    public PropertyResponse getProperty(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        return propertyService.getPropertyById(id, userId);
    }

    @Operation(summary = "Get Public Availability & Booked Date Ranges for a property")
    @GetMapping("/{id}/availability")
    public stayops_api.dto.PublicPropertyAvailabilityDTO getPublicAvailability(@PathVariable Long id) {
        return propertyService.getPublicAvailability(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProperty(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        propertyService.deleteProperty(id, userId);
    }

    @PostMapping(value = "/{id}/photos", consumes = "multipart/form-data")
    public String uploadPropertyPhoto(
            @PathVariable Long id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        return propertyService.uploadPhoto(id, userId, file);
    }

    @DeleteMapping("/{id}/photos")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePropertyPhoto(
            @PathVariable Long id,
            @RequestParam("url") String url,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        propertyService.deletePhoto(id, userId, url);
    }

    @PutMapping("/{id}/room-types")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRoomTypes(
            @PathVariable Long id,
            @RequestBody stayops_api.dto.RoomTypesRequest request,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        propertyService.updateRoomTypes(id, userId, request.getRoomTypesJson(), request.getTotalRoomsCount());
    }

    @PutMapping("/{id}")
    public stayops_api.dto.PropertyResponse updateProperty(
            @PathVariable Long id,
            @RequestBody stayops_api.dto.PropertyUpdateRequest request,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        return propertyService.updateProperty(id, userId, request);
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