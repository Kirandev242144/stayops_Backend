package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import stayops_api.dto.GuestCrmSummary;
import stayops_api.dto.GuestProfileDetailResponse;
import stayops_api.dto.GuestProfileResponse;
import stayops_api.dto.GuestProfileUpdateRequest;
import stayops_api.service.GuestProfileService;

import java.util.List;

@Tag(name = "6. Guest CRM & Profiles", description = "Guest CRM summary, stay history, and contact details")
@RestController
public class CrmController {

    private final GuestProfileService guestProfileService;
    private final stayops_api.service.FileUploadService fileUploadService;

    public CrmController(
            GuestProfileService guestProfileService,
            stayops_api.service.FileUploadService fileUploadService
    ) {
        this.guestProfileService = guestProfileService;
        this.fileUploadService = fileUploadService;
    }

    @Operation(summary = "Upload guest identification document (photo / PDF)")
    @PostMapping(value = {"/api/upload/document", "/api/crm/documents/upload"}, consumes = "multipart/form-data")
    public ResponseEntity<java.util.Map<String, String>> uploadDocument(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file
    ) {
        String fileUrl = fileUploadService.storeFile(file);
        java.util.Map<String, String> response = new java.util.HashMap<>();
        response.put("url", fileUrl);
        response.put("fileName", file.getOriginalFilename());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get CRM List of all Guests with total stays & spends")
    @GetMapping({"/api/crm/guests", "/api/guests"})
    public ResponseEntity<List<GuestCrmSummary>> getAllGuestSummaries(
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        List<GuestCrmSummary> summaries = guestProfileService.getAllGuestSummaries(userId);
        return ResponseEntity.ok(summaries);
    }

    @PostMapping({"/api/crm/guests", "/api/guests"})
    public ResponseEntity<GuestProfileResponse> createGuestProfile(
            @RequestBody java.util.Map<String, String> payload,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        GuestProfileResponse created = guestProfileService.createGuestProfile(
                payload.get("name"),
                payload.get("email"),
                payload.get("phone"),
                payload.get("idType"),
                payload.get("idNumber"),
                payload.get("idDocumentUrl"),
                payload.get("idDocuments"),
                userId
        );
        return ResponseEntity.ok(created);
    }

    @GetMapping({"/api/crm/guests/{id}", "/api/guests/{id}"})
    public ResponseEntity<GuestProfileDetailResponse> getGuestProfileDetail(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        GuestProfileDetailResponse detail = guestProfileService.getGuestProfileDetail(id, userId);
        return ResponseEntity.ok(detail);
    }

    @PutMapping({"/api/crm/guests/{id}", "/api/guests/{id}"})
    public ResponseEntity<GuestProfileResponse> updateGuestProfile(
            @PathVariable Long id,
            @RequestBody GuestProfileUpdateRequest request,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        GuestProfileResponse updated = guestProfileService.updateGuestProfile(id, userId, request);
        return ResponseEntity.ok(updated);
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
