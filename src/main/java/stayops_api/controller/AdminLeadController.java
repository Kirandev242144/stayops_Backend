package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import stayops_api.dto.*;
import stayops_api.entity.AdminAuditLog;
import stayops_api.entity.LeadActivity;
import stayops_api.entity.LeadNote;
import stayops_api.entity.SalesLead;
import stayops_api.entity.User;
import stayops_api.repository.AdminAuditLogRepository;
import stayops_api.repository.LeadActivityRepository;
import stayops_api.repository.LeadNoteRepository;
import stayops_api.repository.SalesLeadRepository;
import stayops_api.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag(name = "12. Admin Console - Sales Leads", description = "Platform Admin Lead Management, Demo Scheduling, and Tenant Conversion APIs")
@RestController
@RequestMapping("/api/admin/leads")
@CrossOrigin(origins = "*")
public class AdminLeadController {

    private final SalesLeadRepository salesLeadRepository;
    private final LeadNoteRepository leadNoteRepository;
    private final LeadActivityRepository leadActivityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminAuditLogRepository adminAuditLogRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
    private static final DateTimeFormatter DATE_ONLY = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public AdminLeadController(
            SalesLeadRepository salesLeadRepository,
            LeadNoteRepository leadNoteRepository,
            LeadActivityRepository leadActivityRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AdminAuditLogRepository adminAuditLogRepository
    ) {
        this.salesLeadRepository = salesLeadRepository;
        this.leadNoteRepository = leadNoteRepository;
        this.leadActivityRepository = leadActivityRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminAuditLogRepository = adminAuditLogRepository;
    }

    /**
     * Get paginated and filtered list of leads.
     */
    @Operation(summary = "Get paginated, filtered, and searched sales leads")
    @GetMapping
    public ResponseEntity<Page<AdminLeadDTO>> getLeads(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) Integer minProperties,
            @RequestParam(required = false) Integer maxProperties,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String assignedAdmin,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Specification<SalesLead> spec = buildSpecification(search, status, propertyType, minProperties, maxProperties, source, assignedAdmin);
        Page<SalesLead> leadPage = salesLeadRepository.findAll(spec, pageable);

        Page<AdminLeadDTO> dtoPage = leadPage.map(this::mapToSummaryDTO);
        return ResponseEntity.ok(dtoPage);
    }

    /**
     * Get real-time summary KPI metrics calculated from database.
     */
    @Operation(summary = "Get lead metrics calculated directly from database records")
    @GetMapping("/summary")
    public ResponseEntity<AdminLeadSummaryDTO> getSummary() {
        long totalLeads = salesLeadRepository.count();
        long newLeads = salesLeadRepository.countByStatus("NEW");
        long demosScheduled = salesLeadRepository.countDemosScheduled();
        long followUpsDue = salesLeadRepository.countFollowUpsDue(LocalDateTime.now());
        long convertedLeads = salesLeadRepository.countByStatus("CONVERTED");

        AdminLeadSummaryDTO summary = new AdminLeadSummaryDTO(totalLeads, newLeads, demosScheduled, followUpsDue, convertedLeads);
        return ResponseEntity.ok(summary);
    }

    /**
     * Get full lead details including notes and activity timeline.
     */
    @Operation(summary = "Get single lead details with notes and activity history")
    @GetMapping("/{id}")
    public ResponseEntity<AdminLeadDTO> getLeadById(@PathVariable Long id) {
        return salesLeadRepository.findById(id)
                .map(this::mapToFullDetailDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update lead status (e.g. CONTACTED, NOT_INTERESTED).
     */
    @Operation(summary = "Update lead status and record change in audit timeline")
    @PatchMapping("/{id}/status")
    public ResponseEntity<AdminLeadDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody LeadStatusUpdateRequest request
    ) {
        SalesLead lead = salesLeadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found with id: " + id));

        String oldStatus = lead.getStatus();
        String newStatus = request.getStatus().trim().toUpperCase();

        lead.setStatus(newStatus);
        lead.setUpdatedAt(LocalDateTime.now());
        SalesLead saved = salesLeadRepository.save(lead);

        // Record activity
        String detail = request.getNote() != null && !request.getNote().isBlank()
                ? request.getNote().trim()
                : String.format("Status changed from %s to %s by admin.", oldStatus, newStatus);

        leadActivityRepository.save(new LeadActivity(
                id,
                "admin@stayops.in",
                "STATUS_CHANGED",
                oldStatus,
                newStatus,
                detail
        ));

        // If a note was included with status change, save it
        if (request.getNote() != null && !request.getNote().isBlank()) {
            leadNoteRepository.save(new LeadNote(id, "admin@stayops.in", request.getNote().trim()));
        }

        return ResponseEntity.ok(mapToFullDetailDTO(saved));
    }

    /**
     * Schedule a product demo.
     */
    @Operation(summary = "Schedule demo date, time, and meeting details")
    @PostMapping("/{id}/schedule-demo")
    public ResponseEntity<AdminLeadDTO> scheduleDemo(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleDemoRequest request
    ) {
        SalesLead lead = salesLeadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found with id: " + id));

        String previousStatus = lead.getStatus();
        lead.setDemoScheduledAt(request.getDemoScheduledAt());
        lead.setDemoTimeZone(request.getDemoTimeZone() != null ? request.getDemoTimeZone() : "Asia/Kolkata");
        lead.setDemoMeetingUrl(request.getDemoMeetingUrl() != null ? request.getDemoMeetingUrl().trim() : null);
        lead.setStatus("DEMO_SCHEDULED");
        lead.setUpdatedAt(LocalDateTime.now());

        SalesLead saved = salesLeadRepository.save(lead);

        String activityNote = String.format("Demo scheduled for %s (%s). Meeting URL: %s",
                request.getDemoScheduledAt().format(FORMATTER),
                lead.getDemoTimeZone(),
                lead.getDemoMeetingUrl() != null ? lead.getDemoMeetingUrl() : "None provided");

        leadActivityRepository.save(new LeadActivity(
                id,
                "admin@stayops.in",
                "DEMO_SCHEDULED",
                previousStatus,
                "DEMO_SCHEDULED",
                activityNote
        ));

        if (request.getNote() != null && !request.getNote().isBlank()) {
            leadNoteRepository.save(new LeadNote(id, "admin@stayops.in", request.getNote().trim()));
        }

        return ResponseEntity.ok(mapToFullDetailDTO(saved));
    }

    /**
     * Schedule a follow-up date and time.
     */
    @Operation(summary = "Schedule next follow-up action date")
    @PostMapping("/{id}/follow-ups")
    public ResponseEntity<AdminLeadDTO> scheduleFollowUp(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleFollowUpRequest request
    ) {
        SalesLead lead = salesLeadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found with id: " + id));

        String oldStatus = lead.getStatus();
        lead.setNextFollowUpAt(request.getNextFollowUpAt());
        if ("NEW".equals(oldStatus) || "CONTACTED".equals(oldStatus)) {
            lead.setStatus("FOLLOW_UP_REQUIRED");
        }
        lead.setUpdatedAt(LocalDateTime.now());

        SalesLead saved = salesLeadRepository.save(lead);

        String detail = String.format("Follow-up scheduled for %s. %s",
                request.getNextFollowUpAt().format(FORMATTER),
                request.getNote() != null ? request.getNote() : "");

        leadActivityRepository.save(new LeadActivity(
                id,
                "admin@stayops.in",
                "FOLLOW_UP_SCHEDULED",
                oldStatus,
                lead.getStatus(),
                detail.trim()
        ));

        if (request.getNote() != null && !request.getNote().isBlank()) {
            leadNoteRepository.save(new LeadNote(id, "admin@stayops.in", request.getNote().trim()));
        }

        return ResponseEntity.ok(mapToFullDetailDTO(saved));
    }

    /**
     * Add an internal note to the lead.
     */
    @Operation(summary = "Add an internal note to a lead")
    @PostMapping("/{id}/notes")
    public ResponseEntity<LeadNoteDTO> addNote(
            @PathVariable Long id,
            @Valid @RequestBody LeadNoteRequest request
    ) {
        if (!salesLeadRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        LeadNote note = new LeadNote(id, "admin@stayops.in", request.getNote().trim());
        LeadNote savedNote = leadNoteRepository.save(note);

        leadActivityRepository.save(new LeadActivity(
                id,
                "admin@stayops.in",
                "NOTE_ADDED",
                null,
                null,
                "Internal note added by admin."
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToNoteDTO(savedNote));
    }

    /**
     * Convert lead to a registered StayOps Tenant account.
     */
    @Operation(summary = "Convert lead to tenant account without duplicates")
    @PostMapping("/{id}/convert")
    @Transactional
    public ResponseEntity<AdminLeadDTO> convertLead(
            @PathVariable Long id,
            @RequestBody ConvertLeadRequest request
    ) {
        SalesLead lead = salesLeadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found with id: " + id));

        if ("CONVERTED".equals(lead.getStatus()) && lead.getConvertedTenantId() != null) {
            return ResponseEntity.badRequest().body(mapToFullDetailDTO(lead));
        }

        Long tenantId;

        if (request.getExistingTenantId() != null) {
            // Link to existing verified tenant
            User existing = userRepository.findById(request.getExistingTenantId())
                    .orElseThrow(() -> new RuntimeException("Existing tenant not found with id: " + request.getExistingTenantId()));
            tenantId = existing.getId();
        } else {
            // Check if account already exists with same email
            Optional<User> existingUser = userRepository.findByEmail(lead.getEmail());
            if (existingUser.isPresent()) {
                tenantId = existingUser.get().getId();
            } else {
                // Create new User account
                User newUser = new User();
                newUser.setName(lead.getFullName());
                newUser.setEmail(lead.getEmail());
                newUser.setPhone(lead.getPhone());
                newUser.setPassword(passwordEncoder.encode("StayOps@2026"));
                newUser.setRole("Owner");
                newUser.setStatus("Active");
                newUser.setPlan(lead.getPropertyCount() > 1 ? "hub" : "pro");
                newUser.setCreatedAt(LocalDateTime.now());
                User savedUser = userRepository.save(newUser);
                tenantId = savedUser.getId();
            }
        }

        // Update lead state
        lead.setConvertedTenantId(tenantId);
        lead.setConvertedAt(LocalDateTime.now());
        lead.setStatus("CONVERTED");
        lead.setUpdatedAt(LocalDateTime.now());

        SalesLead savedLead = salesLeadRepository.save(lead);

        // Record in lead activity
        leadActivityRepository.save(new LeadActivity(
                id,
                "admin@stayops.in",
                "LEAD_CONVERTED",
                "PENDING",
                "CONVERTED",
                String.format("Converted lead to Tenant account ID TEN-%03d.", tenantId)
        ));

        // Record in tenant audit log
        adminAuditLogRepository.save(new AdminAuditLog(
                tenantId,
                "admin@stayops.in",
                "Lead Converted",
                String.format("Tenant account linked from sales lead #%d (%s - %s).", id, lead.getFullName(), lead.getBusinessName())
        ));

        return ResponseEntity.ok(mapToFullDetailDTO(savedLead));
    }

    /**
     * Export filtered leads to CSV safely with formula injection protection.
     */
    @Operation(summary = "Export filtered leads to downloadable CSV")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportLeadsToCsv(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) Integer minProperties,
            @RequestParam(required = false) Integer maxProperties,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String assignedAdmin
    ) {
        Specification<SalesLead> spec = buildSpecification(search, status, propertyType, minProperties, maxProperties, source, assignedAdmin);
        List<SalesLead> list = salesLeadRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));

        StringBuilder csv = new StringBuilder();
        csv.append("Lead ID,Full Name,Business Name,Email,Phone,Properties,Property Type,City,State,Status,Source,Demo Date,Next Follow-up,Converted Tenant ID,Created At\n");

        for (SalesLead l : list) {
            csv.append(escapeCsv(String.valueOf(l.getId()))).append(",");
            csv.append(escapeCsv(l.getFullName())).append(",");
            csv.append(escapeCsv(l.getBusinessName())).append(",");
            csv.append(escapeCsv(l.getEmail())).append(",");
            csv.append(escapeCsv(l.getPhone())).append(",");
            csv.append(l.getPropertyCount()).append(",");
            csv.append(escapeCsv(l.getPropertyType())).append(",");
            csv.append(escapeCsv(l.getCity())).append(",");
            csv.append(escapeCsv(l.getState())).append(",");
            csv.append(escapeCsv(l.getStatus())).append(",");
            csv.append(escapeCsv(l.getSource())).append(",");
            csv.append(escapeCsv(l.getDemoScheduledAt() != null ? l.getDemoScheduledAt().format(FORMATTER) : "—")).append(",");
            csv.append(escapeCsv(l.getNextFollowUpAt() != null ? l.getNextFollowUpAt().format(FORMATTER) : "—")).append(",");
            csv.append(escapeCsv(l.getConvertedTenantId() != null ? "TEN-" + l.getConvertedTenantId() : "—")).append(",");
            csv.append(escapeCsv(l.getCreatedAt() != null ? l.getCreatedAt().format(FORMATTER) : "—")).append("\n");
        }

        byte[] bytes = csv.toString().getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", "stayops_sales_leads_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    // ── Helper mapping methods ──────────────────────────────────────────────

    private AdminLeadDTO mapToSummaryDTO(SalesLead l) {
        AdminLeadDTO dto = new AdminLeadDTO();
        populateBasicFields(dto, l);
        return dto;
    }

    private AdminLeadDTO mapToFullDetailDTO(SalesLead l) {
        AdminLeadDTO dto = new AdminLeadDTO();
        populateBasicFields(dto, l);

        // Fetch notes
        List<LeadNote> notes = leadNoteRepository.findByLeadIdOrderByCreatedAtDesc(l.getId());
        List<LeadNoteDTO> noteDTOs = notes.stream().map(this::mapToNoteDTO).toList();
        dto.setNotes(noteDTOs);

        // Fetch activity
        List<LeadActivity> activities = leadActivityRepository.findByLeadIdOrderByCreatedAtDesc(l.getId());
        List<LeadActivityDTO> activityDTOs = activities.stream().map(this::mapToActivityDTO).toList();
        dto.setActivity(activityDTOs);

        return dto;
    }

    private void populateBasicFields(AdminLeadDTO dto, SalesLead l) {
        dto.setId(l.getId());
        dto.setFullName(l.getFullName());
        dto.setBusinessName(l.getBusinessName());
        dto.setEmail(l.getEmail());
        dto.setPhone(l.getPhone());
        dto.setPropertyCount(l.getPropertyCount());
        dto.setPropertyType(l.getPropertyType());
        dto.setCity(l.getCity());
        dto.setState(l.getState());
        dto.setMessage(l.getMessage());
        dto.setPreferredContactMethod(l.getPreferredContactMethod());
        dto.setStatus(l.getStatus());
        dto.setSource(l.getSource());
        dto.setAssignedAdminEmail(l.getAssignedAdminEmail());

        dto.setDemoScheduledAt(l.getDemoScheduledAt());
        dto.setDemoScheduledAtFormatted(l.getDemoScheduledAt() != null ? l.getDemoScheduledAt().format(FORMATTER) : null);
        dto.setDemoTimeZone(l.getDemoTimeZone());
        dto.setDemoMeetingUrl(l.getDemoMeetingUrl());

        dto.setNextFollowUpAt(l.getNextFollowUpAt());
        dto.setNextFollowUpAtFormatted(l.getNextFollowUpAt() != null ? l.getNextFollowUpAt().format(FORMATTER) : null);

        boolean isDue = l.getNextFollowUpAt() != null
                && l.getNextFollowUpAt().isBefore(LocalDateTime.now())
                && !"CONVERTED".equals(l.getStatus())
                && !"NOT_INTERESTED".equals(l.getStatus());
        dto.setFollowUpDue(isDue);

        dto.setConvertedTenantId(l.getConvertedTenantId());
        dto.setConvertedAt(l.getConvertedAt());
        dto.setConvertedAtFormatted(l.getConvertedAt() != null ? l.getConvertedAt().format(FORMATTER) : null);

        dto.setCreatedAt(l.getCreatedAt());
        dto.setCreatedAtFormatted(l.getCreatedAt() != null ? l.getCreatedAt().format(DATE_ONLY) : "—");
        dto.setUpdatedAt(l.getUpdatedAt());
    }

    private LeadNoteDTO mapToNoteDTO(LeadNote n) {
        return new LeadNoteDTO(n.getId(), n.getLeadId(), n.getAuthorAdminEmail(), n.getNote(), n.getCreatedAt());
    }

    private LeadActivityDTO mapToActivityDTO(LeadActivity a) {
        return new LeadActivityDTO(a.getId(), a.getLeadId(), a.getActorAdminEmail(), a.getEventType(), a.getOldValue(), a.getNewValue(), a.getMetadata(), a.getCreatedAt());
    }

    private Specification<SalesLead> buildSpecification(
            String search,
            String status,
            String propertyType,
            Integer minProperties,
            Integer maxProperties,
            String source,
            String assignedAdmin
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(root.get("fullName")), pattern),
                        cb.like(cb.lower(root.get("businessName")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern),
                        cb.like(cb.lower(root.get("phone")), pattern),
                        cb.like(cb.lower(root.get("city")), pattern)
                );
                predicates.add(searchPredicate);
            }

            if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
                predicates.add(cb.equal(cb.upper(root.get("status")), status.trim().toUpperCase()));
            }

            if (propertyType != null && !propertyType.isBlank() && !"ALL".equalsIgnoreCase(propertyType)) {
                predicates.add(cb.equal(cb.lower(root.get("propertyType")), propertyType.trim().toLowerCase()));
            }

            if (minProperties != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("propertyCount"), minProperties));
            }

            if (maxProperties != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("propertyCount"), maxProperties));
            }

            if (source != null && !source.isBlank() && !"ALL".equalsIgnoreCase(source)) {
                predicates.add(cb.equal(cb.lower(root.get("source")), source.trim().toLowerCase()));
            }

            if (assignedAdmin != null && !assignedAdmin.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("assignedAdminEmail")), assignedAdmin.trim().toLowerCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Sanitizes CSV values against CSV injection (=, +, -, @) and wraps in quotes.
     */
    private String escapeCsv(String value) {
        if (value == null) return "\"\"";
        String str = value.trim();
        if (str.startsWith("=") || str.startsWith("+") || str.startsWith("-") || str.startsWith("@")) {
            str = "'" + str; // neutralize formula prefix
        }
        return "\"" + str.replace("\"", "\"\"") + "\"";
    }
}
