package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stayops_api.dto.PublicDemoRequest;
import stayops_api.dto.PublicDemoResponse;
import stayops_api.entity.LeadActivity;
import stayops_api.entity.SalesLead;
import stayops_api.repository.LeadActivityRepository;
import stayops_api.repository.SalesLeadRepository;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "11. Public - Demo Requests", description = "Public endpoints for marketing website demo enquiries")
@RestController
@RequestMapping("/api/public/demo-requests")
@CrossOrigin(origins = "*")
public class PublicDemoController {

    private final SalesLeadRepository salesLeadRepository;
    private final LeadActivityRepository leadActivityRepository;

    public PublicDemoController(
            SalesLeadRepository salesLeadRepository,
            LeadActivityRepository leadActivityRepository
    ) {
        this.salesLeadRepository = salesLeadRepository;
        this.leadActivityRepository = leadActivityRepository;
    }

    @Operation(summary = "Submit a product demo request from the marketing website")
    @PostMapping
    public ResponseEntity<?> submitDemoRequest(@Valid @RequestBody PublicDemoRequest request) {
        // Validation checks
        if (request.getFullName() == null || request.getFullName().isBlank()) {
            return ResponseEntity.badRequest().body(new PublicDemoResponse(null, false, "Full name is required."));
        }
        if (request.getBusinessName() == null || request.getBusinessName().isBlank()) {
            return ResponseEntity.badRequest().body(new PublicDemoResponse(null, false, "Business or property name is required."));
        }
        if (request.getEmail() == null || !request.getEmail().contains("@")) {
            return ResponseEntity.badRequest().body(new PublicDemoResponse(null, false, "A valid email address is required."));
        }
        if (request.getPhone() == null || request.getPhone().trim().length() < 7) {
            return ResponseEntity.badRequest().body(new PublicDemoResponse(null, false, "A valid phone number is required."));
        }
        if (request.getPropertyCount() == null || request.getPropertyCount() < 1) {
            return ResponseEntity.badRequest().body(new PublicDemoResponse(null, false, "Number of properties must be at least 1."));
        }
        if (request.getPropertyType() == null || request.getPropertyType().isBlank()) {
            return ResponseEntity.badRequest().body(new PublicDemoResponse(null, false, "Property type is required."));
        }
        if (request.getCity() == null || request.getCity().isBlank()) {
            return ResponseEntity.badRequest().body(new PublicDemoResponse(null, false, "City is required."));
        }
        if (request.getState() == null || request.getState().isBlank()) {
            return ResponseEntity.badRequest().body(new PublicDemoResponse(null, false, "State is required."));
        }
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        String normalizedPhone = request.getPhone().replaceAll("[^0-9+]", "");

        // Spam / duplicate protection: check if same email submitted within last 3 minutes
        LocalDateTime threeMinutesAgo = LocalDateTime.now().minusMinutes(3);
        List<SalesLead> recent = salesLeadRepository.findRecentByEmail(normalizedEmail, threeMinutesAgo);
        if (!recent.isEmpty()) {
            SalesLead existing = recent.get(0);
            return ResponseEntity.status(HttpStatus.OK).body(new PublicDemoResponse(
                    existing.getId(),
                    true,
                    "Thank you for your interest in StayOps! Your demo request has been received. Our team will contact you soon."
            ));
        }

        // Create new SalesLead
        SalesLead lead = new SalesLead();
        lead.setFullName(request.getFullName().trim());
        lead.setBusinessName(request.getBusinessName().trim());
        lead.setEmail(request.getEmail().trim());
        lead.setNormalizedEmail(normalizedEmail);
        lead.setPhone(request.getPhone().trim());
        lead.setNormalizedPhone(normalizedPhone);
        lead.setPropertyCount(request.getPropertyCount() != null ? request.getPropertyCount() : 1);
        lead.setPropertyType(request.getPropertyType() != null ? request.getPropertyType().trim() : "Homestay");
        lead.setCity(request.getCity().trim());
        lead.setState(request.getState().trim());
        lead.setMessage(request.getMessage() != null ? request.getMessage().trim() : null);
        lead.setPreferredContactMethod(request.getPreferredContactMethod() != null ? request.getPreferredContactMethod() : "WhatsApp");
        lead.setStatus("NEW");
        lead.setSource("Marketing Website");
        lead.setCreatedAt(LocalDateTime.now());
        lead.setUpdatedAt(LocalDateTime.now());

        SalesLead savedLead = salesLeadRepository.save(lead);

        // Record initial activity history
        LeadActivity activity = new LeadActivity(
                savedLead.getId(),
                "System",
                "LEAD_CREATED",
                null,
                "NEW",
                "Enquiry submitted via Marketing Website Book a Demo form."
        );
        leadActivityRepository.save(activity);

        return ResponseEntity.status(HttpStatus.CREATED).body(new PublicDemoResponse(
                savedLead.getId(),
                true,
                "Thank you for your interest in StayOps! Your demo request has been received. Our team will contact you soon."
        ));
    }
}
