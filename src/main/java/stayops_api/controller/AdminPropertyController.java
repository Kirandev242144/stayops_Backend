package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import stayops_api.dto.AdminAuditLogDTO;
import stayops_api.dto.AdminPropertyDTO;
import stayops_api.dto.PropertyRequest;
import stayops_api.entity.AdminAuditLog;
import stayops_api.entity.Property;
import stayops_api.entity.PropertyPhoto;
import stayops_api.entity.User;
import stayops_api.repository.AdminAuditLogRepository;
import stayops_api.repository.PropertyPhotoRepository;
import stayops_api.repository.PropertyRepository;
import stayops_api.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag(name = "9. Admin Console - Properties Directory", description = "Platform Admin Property & Owner Management APIs")
@RestController
@RequestMapping("/api/admin/properties")
@CrossOrigin(origins = "*")
public class AdminPropertyController {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PropertyPhotoRepository propertyPhotoRepository;
    private final AdminAuditLogRepository auditLogRepository;

    public AdminPropertyController(
            PropertyRepository propertyRepository,
            UserRepository userRepository,
            PropertyPhotoRepository propertyPhotoRepository,
            AdminAuditLogRepository auditLogRepository
    ) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.propertyPhotoRepository = propertyPhotoRepository;
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Get all properties from database across all owners.
     */
    @Operation(summary = "Get all registered properties with owner information from the database")
    @GetMapping
    public List<AdminPropertyDTO> getAllProperties() {
        List<Property> properties = propertyRepository.findAll();
        List<AdminPropertyDTO> result = new ArrayList<>();

        for (Property p : properties) {
            result.add(mapToAdminDTO(p));
        }

        return result;
    }

    /**
     * Get single property by ID with owner information and audit log.
     */
    @Operation(summary = "Get single property by ID")
    @GetMapping("/{id}")
    public ResponseEntity<AdminPropertyDTO> getPropertyById(@PathVariable Long id) {
        return propertyRepository.findById(id)
                .map(p -> {
                    AdminPropertyDTO dto = mapToAdminDTO(p);
                    if (p.getUser() != null) {
                        List<AdminAuditLog> logs = auditLogRepository.findByTenantIdOrderByCreatedAtDesc(p.getUser().getId());
                        List<AdminAuditLogDTO> logDTOs = new ArrayList<>();
                        for (AdminAuditLog log : logs) {
                            logDTOs.add(new AdminAuditLogDTO(
                                    log.getId(), log.getAction(), log.getDetail(),
                                    log.getActorEmail(), log.getCreatedAt()
                            ));
                        }
                        dto.setAuditLog(logDTOs);
                    }
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update property status (Active, Suspended, Review, Setup Pending).
     */
    @Operation(summary = "Update status of a property")
    @RequestMapping(value = "/{id}/status", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<AdminPropertyDTO> updatePropertyStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String newStatus = body.get("status");
        if (newStatus == null || newStatus.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));

        String oldStatus = property.getStatus();
        property.setStatus(newStatus.trim());
        Property saved = propertyRepository.save(property);

        if (property.getUser() != null) {
            try {
                auditLogRepository.save(new AdminAuditLog(
                        property.getUser().getId(),
                        "admin@stayops.in",
                        "PROPERTY_STATUS_CHANGED",
                        "Property '" + property.getName() + "' status changed from " + oldStatus + " to " + newStatus.trim()
                ));
            } catch (Exception ignored) {}
        }

        return ResponseEntity.ok(mapToAdminDTO(saved));
    }

    /**
     * Create a new property as admin directly in the database.
     */
    @Operation(summary = "Admin create property for an owner")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminPropertyDTO createProperty(@RequestBody PropertyRequest request) {
        // Find owner or default to the first owner in the database
        List<User> users = userRepository.findAll();
        User targetOwner = users.isEmpty() ? null : users.get(0);

        if (targetOwner == null) {
            throw new RuntimeException("No property owners found in the database. Please register an owner first.");
        }

        Property property = new Property();
        property.setUser(targetOwner);
        property.setName(request.getName());
        property.setType(request.getType() != null ? request.getType() : "Homestay");
        property.setDescription(request.getDescription());
        property.setAddress(request.getAddress());
        property.setCity(request.getCity());
        property.setState(request.getState());
        property.setPostalCode(request.getPostalCode());
        property.setPermitId(request.getPermitId());
        property.setContactEmail(request.getContactEmail() != null ? request.getContactEmail() : targetOwner.getEmail());
        property.setContactPhone(request.getContactPhone());
        property.setLocation((request.getCity() != null ? request.getCity() : "") + 
                (request.getState() != null ? ", " + request.getState() : ""));
        
        property.setCreatedAt(LocalDateTime.now());
        property.setStatus("Active");
        property.setBaseRate(request.getBaseRate() != null ? request.getBaseRate() : 3500);
        property.setIsEntireVilla(request.getIsEntireVilla() != null ? request.getIsEntireVilla() : false);
        property.setRooms(request.getRooms() != null ? request.getRooms() : 1);

        if (request.getRoomTypesJson() != null && !request.getRoomTypesJson().isBlank()) {
            property.setRoomTypes(request.getRoomTypesJson());
        }
        if (request.getOperationsJson() != null && !request.getOperationsJson().isBlank()) {
            property.setOperations(request.getOperationsJson());
        }
        if (request.getPropertyAmenitiesJson() != null && !request.getPropertyAmenitiesJson().isBlank()) {
            property.setPropertyAmenities(request.getPropertyAmenitiesJson());
        }

        Property saved = propertyRepository.save(property);
        return mapToAdminDTO(saved);
    }

    /**
     * Delete property by ID.
     */
    @Operation(summary = "Admin delete property")
    @DeleteMapping("/{id}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProperty(@PathVariable Long id) {
        if (!propertyRepository.existsById(id)) {
            throw new RuntimeException("Property not found with id: " + id);
        }
        propertyPhotoRepository.deleteByProperty_Id(id);
        propertyRepository.deleteById(id);
    }

    private AdminPropertyDTO mapToAdminDTO(Property p) {
        AdminPropertyDTO dto = new AdminPropertyDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setType(p.getType());
        dto.setCategory(p.getType() != null ? p.getType() : "Homestay");
        dto.setCode("PROP-" + (p.getId() != null ? p.getId() : "0"));
        dto.setLocation(p.getLocation());
        dto.setAddress(p.getAddress());
        dto.setCity(p.getCity());
        dto.setState(p.getState());
        dto.setPostalCode(p.getPostalCode());
        dto.setPermitId(p.getPermitId());
        dto.setContactEmail(p.getContactEmail());
        dto.setContactPhone(p.getContactPhone());
        dto.setRooms(p.getRooms() != null ? p.getRooms() : 1);
        dto.setStatus(p.getStatus() != null ? p.getStatus() : "Active");
        dto.setBaseRate(p.getBaseRate() != null ? p.getBaseRate() : 0);
        dto.setIsEntireVilla(p.getIsEntireVilla() != null ? p.getIsEntireVilla() : false);
        dto.setRoomTypes(p.getRoomTypes());
        dto.setPropertyAmenities(p.getPropertyAmenities());
        dto.setPricing(p.getPricing());
        dto.setOperations(p.getOperations());
        dto.setStorefront(p.getStorefront());
        dto.setCreatedAt(p.getCreatedAt());

        // Derive storefront subdomain slug
        String slug = p.getStorefront();
        if ((slug == null || slug.isBlank()) && p.getOperations() != null) {
            try {
                // Quick parse if operations contains subdomain
                int subIndex = p.getOperations().indexOf("\"subdomain\"");
                if (subIndex != -1) {
                    int colonIndex = p.getOperations().indexOf(":", subIndex);
                    int quoteStart = p.getOperations().indexOf("\"", colonIndex);
                    int quoteEnd = p.getOperations().indexOf("\"", quoteStart + 1);
                    if (quoteStart != -1 && quoteEnd != -1) {
                        slug = p.getOperations().substring(quoteStart + 1, quoteEnd);
                    }
                }
            } catch (Exception ignored) {}
        }
        if (slug == null || slug.isBlank()) {
            slug = p.getName() != null ? p.getName().trim().toLowerCase().replaceAll("[^a-z0-9]", "") : "property-" + p.getId();
        }
        dto.setSlug(slug);

        // Photos
        try {
            List<String> photos = propertyPhotoRepository.findByProperty_Id(p.getId())
                    .stream().map(PropertyPhoto::getUrl).toList();
            dto.setPropertyPhotos(photos);
        } catch (Exception e) {
            dto.setPropertyPhotos(new ArrayList<>());
        }

        // Owner identity & Subscription Plan
        if (p.getUser() != null) {
            dto.setOwnerId(p.getUser().getId());
            dto.setOwnerName(p.getUser().getName());
            dto.setOwnerEmail(p.getUser().getEmail());
            dto.setOwnerPhone(p.getContactPhone() != null ? p.getContactPhone() : "");

            long ownerProps = propertyRepository.countByUserId(p.getUser().getId());
            if (ownerProps > 1) {
                dto.setSubscriptionPlan("Multi-Property Hub");
            } else if (ownerProps == 1) {
                dto.setSubscriptionPlan("Pro Growth");
            } else {
                dto.setSubscriptionPlan("14-Day Free Trial");
            }
        } else {
            dto.setSubscriptionPlan("Standard Host");
        }

        return dto;
    }
}
