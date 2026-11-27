package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import stayops_api.entity.RoomHousekeepingStatus;
import stayops_api.repository.RoomHousekeepingStatusRepository;
import stayops_api.websocket.WebSocketBroadcaster;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "9. Housekeeping & Room Status", description = "Room cleaning states, staff assignments, and maintenance logs")
@RestController
@RequestMapping("/api/housekeeping")
public class HousekeepingController {

    private final RoomHousekeepingStatusRepository housekeepingRepository;
    private final stayops_api.repository.ReservationRepository reservationRepository;
    private final WebSocketBroadcaster webSocketBroadcaster;

    public HousekeepingController(
            RoomHousekeepingStatusRepository housekeepingRepository,
            stayops_api.repository.ReservationRepository reservationRepository,
            WebSocketBroadcaster webSocketBroadcaster
    ) {
        this.housekeepingRepository = housekeepingRepository;
        this.reservationRepository = reservationRepository;
        this.webSocketBroadcaster = webSocketBroadcaster;
    }

    @Operation(summary = "Get housekeeping status for all rooms")
    @GetMapping
    public List<RoomHousekeepingStatus> getAllStatuses(
            @RequestParam(required = false) Long propertyId
    ) {
        // Auto-sync checked-out reservations to DIRTY status
        try {
            List<stayops_api.entity.Reservation> reservations = reservationRepository.findAll();
            List<RoomHousekeepingStatus> allHk = housekeepingRepository.findAll();
            for (stayops_api.entity.Reservation r : reservations) {
                String st = r.getStatus() != null ? r.getStatus().toLowerCase() : "";
                if (st.contains("checkout") || st.contains("check_out") || st.contains("checked out") || st.contains("depart") || st.contains("completed")) {
                    if (r.getRoomUnit() != null && !r.getRoomUnit().isBlank()) {
                        Long pId = r.getProperty() != null ? r.getProperty().getId() : 1L;
                        String roomNum = r.getRoomUnit().trim();
                        String cleanName = roomNum.split("—")[0].split("-")[0].trim();

                        ensureDirtyIfUncleaned(pId, roomNum, r);
                        ensureDirtyIfUncleaned(pId, cleanName, r);
                        ensureDirtyIfUncleaned(pId, pId + "-0", r);
                        ensureDirtyIfUncleaned(pId, pId + "-1", r);
                        ensureDirtyIfUncleaned(pId, pId + "-2", r);
                        ensureDirtyIfUncleaned(pId, pId + "-" + roomNum, r);
                        ensureDirtyIfUncleaned(pId, pId + "-" + cleanName, r);

                        for (RoomHousekeepingStatus hk : allHk) {
                            if (hk.getPropertyId().equals(pId)) {
                                String hkRoom = hk.getRoomId();
                                String hkRoomClean = hkRoom.split("—")[0].split("-")[0].trim();
                                if (hkRoom.equalsIgnoreCase(roomNum) || 
                                    hkRoomClean.equalsIgnoreCase(cleanName) || 
                                    hkRoom.toLowerCase().contains(cleanName.toLowerCase()) ||
                                    (hk.getNotes() != null && hk.getNotes().contains("Checked-out guest"))) {
                                    ensureDirtyIfUncleaned(pId, hkRoom, r);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}

        if (propertyId != null) {
            return housekeepingRepository.findByPropertyId(propertyId);
        }
        return housekeepingRepository.findAll();
    }

    private void ensureDirtyIfUncleaned(Long propId, String key, stayops_api.entity.Reservation r) {
        if (key == null || key.isBlank()) return;
        String trimmed = key.trim();
        Optional<RoomHousekeepingStatus> existing = housekeepingRepository.findByPropertyIdAndRoomId(propId, trimmed);
        if (existing.isEmpty()) {
            RoomHousekeepingStatus hk = new RoomHousekeepingStatus(propId, trimmed, "DIRTY", "Turnover required: Checked-out guest (" + r.getGuestName() + ")");
            housekeepingRepository.save(hk);
        } else {
            RoomHousekeepingStatus hk = existing.get();
            boolean needsTurnover = true;
            if ("CLEAN".equalsIgnoreCase(hk.getStatus()) && hk.getPhotoProofUrl() != null && !hk.getPhotoProofUrl().isBlank() && hk.getLastCleanedAt() != null) {
                needsTurnover = false;
            }
            if (needsTurnover && !"OUT_OF_ORDER".equalsIgnoreCase(hk.getStatus())) {
                hk.setStatus("DIRTY");
                hk.setPhotoProofUrl(null);
                hk.setCleanedBy(null);
                hk.setLastCleanedAt(null);
                hk.setNotes("Turnover required: Checked-out guest (" + (r.getGuestName() != null ? r.getGuestName() : "Guest") + ")");
                if (hk.getChecklistJson() != null) {
                    hk.setChecklistJson(resetChecklistJson(hk.getChecklistJson()));
                }
                housekeepingRepository.save(hk);
            }
        }
    }

    @Operation(summary = "Update room housekeeping status (CLEAN, DIRTY, IN_PROGRESS, INSPECTED, OUT_OF_ORDER)")
    @PostMapping("/status")
    public RoomHousekeepingStatus updateRoomStatus(@RequestBody Map<String, Object> payload) {
        Long propertyId = payload.get("propertyId") != null ? Long.valueOf(payload.get("propertyId").toString()) : 1L;
        String roomId = payload.get("roomId") != null ? payload.get("roomId").toString().trim() : "101";
        String status = payload.get("status") != null ? payload.get("status").toString().toUpperCase().trim() : "CLEAN";
        String notes = payload.get("notes") != null ? payload.get("notes").toString() : null;

        String assignedTo = payload.get("assignedTo") != null ? payload.get("assignedTo").toString() : null;
        String taskType = payload.get("taskType") != null ? payload.get("taskType").toString() : null;
        String priority = payload.get("priority") != null ? payload.get("priority").toString() : null;
        String checklistJson = payload.get("checklistJson") != null ? payload.get("checklistJson").toString() : null;
        String photoUrl = payload.get("photoUrl") != null ? payload.get("photoUrl").toString() 
                        : payload.get("photoProofUrl") != null ? payload.get("photoProofUrl").toString() : null;

        Optional<RoomHousekeepingStatus> existing = housekeepingRepository.findByPropertyIdAndRoomId(propertyId, roomId);

        // ENFORCE ACCEPTANCE CRITERIA: Room cannot be changed to "CLEAN" / "Ready" without photo evidence
        if ("CLEAN".equalsIgnoreCase(status) || "READY".equalsIgnoreCase(status) || "INSPECTED".equalsIgnoreCase(status)) {
            String existingPhoto = existing.isPresent() ? existing.get().getPhotoProofUrl() : null;
            String photo = (photoUrl != null && !photoUrl.isBlank()) ? photoUrl : existingPhoto;

            if (photo == null || photo.isBlank()) {
                throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Photo Evidence Required: A room cannot be changed to 'Ready' / 'CLEAN' unless required photo evidence is provided."
                );
            }
        }

        RoomHousekeepingStatus housekeeping = existing.orElseGet(() -> new RoomHousekeepingStatus(propertyId, roomId, status, notes));

        housekeeping.setStatus(status);
        if (notes != null) housekeeping.setNotes(notes);
        if (assignedTo != null) housekeeping.setAssignedTo(assignedTo);
        if (taskType != null) housekeeping.setTaskType(taskType);
        if (priority != null) housekeeping.setPriority(priority);

        if ("DIRTY".equalsIgnoreCase(status) || "OUT_OF_ORDER".equalsIgnoreCase(status)) {
            // Turnover required: clear old photos and reset checklist tasks
            housekeeping.setPhotoProofUrl(null);
            housekeeping.setCleanedBy(null);
            housekeeping.setLastCleanedAt(null);
            if (checklistJson == null && housekeeping.getChecklistJson() != null) {
                housekeeping.setChecklistJson(resetChecklistJson(housekeeping.getChecklistJson()));
            } else if (checklistJson != null) {
                housekeeping.setChecklistJson(resetChecklistJson(checklistJson));
            }
            photoUrl = null;
            checklistJson = housekeeping.getChecklistJson();
        } else {
            if (checklistJson != null) housekeeping.setChecklistJson(checklistJson);
            if (photoUrl != null && !photoUrl.isBlank()) housekeeping.setPhotoProofUrl(photoUrl);
            if ("CLEAN".equalsIgnoreCase(status) || "READY".equalsIgnoreCase(status) || "INSPECTED".equalsIgnoreCase(status)) {
                housekeeping.setLastCleanedAt(LocalDateTime.now());
            }
        }

        RoomHousekeepingStatus saved = housekeepingRepository.save(housekeeping);

        // Sync candidate aliases
        String roomName = payload.get("roomName") != null ? payload.get("roomName").toString().trim() : null;
        syncAliases(propertyId, roomId, roomName, status, notes, assignedTo, taskType, priority, photoUrl, checklistJson);

        webSocketBroadcaster.broadcastHousekeeping("STATUS_UPDATED", saved);

        return saved;
    }

    @Operation(summary = "Assign a staff member to room cleaning with task type & priority")
    @PostMapping("/assign")
    public RoomHousekeepingStatus assignRoom(@RequestBody Map<String, Object> payload) {
        Long propertyId = payload.get("propertyId") != null ? Long.valueOf(payload.get("propertyId").toString()) : 1L;
        String roomId = payload.get("roomId") != null ? payload.get("roomId").toString().trim() : "101";
        String assignedTo = payload.get("assignedTo") != null ? payload.get("assignedTo").toString().trim() : "Caretaker";
        String taskType = payload.get("taskType") != null ? payload.get("taskType").toString().trim() : "TURNOVER";
        String priority = payload.get("priority") != null ? payload.get("priority").toString().trim() : "NORMAL";
        String notes = payload.get("notes") != null ? payload.get("notes").toString() : null;

        Optional<RoomHousekeepingStatus> existing = housekeepingRepository.findByPropertyIdAndRoomId(propertyId, roomId);
        RoomHousekeepingStatus housekeeping = existing.orElseGet(() -> new RoomHousekeepingStatus(propertyId, roomId, "DIRTY", notes));

        housekeeping.setAssignedTo(assignedTo);
        housekeeping.setTaskType(taskType);
        housekeeping.setPriority(priority);
        if (notes != null) housekeeping.setNotes(notes);

        RoomHousekeepingStatus saved = housekeepingRepository.save(housekeeping);

        // Sync aliases
        syncAliases(propertyId, roomId, null, saved.getStatus(), notes, assignedTo, taskType, priority, saved.getPhotoProofUrl(), saved.getChecklistJson());

        webSocketBroadcaster.broadcastHousekeeping("ASSIGNMENT_UPDATED", saved);
        return saved;
    }

    @Operation(summary = "Log a room maintenance defect and optionally mark Out of Order")
    @PostMapping("/maintenance")
    public RoomHousekeepingStatus logMaintenance(@RequestBody Map<String, Object> payload) {
        Long propertyId = payload.get("propertyId") != null ? Long.valueOf(payload.get("propertyId").toString()) : 1L;
        String roomId = payload.get("roomId") != null ? payload.get("roomId").toString().trim() : "101";
        String issue = payload.get("issue") != null ? payload.get("issue").toString() : "Maintenance reported";
        String severity = payload.get("severity") != null ? payload.get("severity").toString().toUpperCase() : "MODERATE";
        boolean markOutOfOrder = payload.get("markOutOfOrder") != null && Boolean.parseBoolean(payload.get("markOutOfOrder").toString());

        Optional<RoomHousekeepingStatus> existing = housekeepingRepository.findByPropertyIdAndRoomId(propertyId, roomId);
        RoomHousekeepingStatus housekeeping = existing.orElseGet(() -> new RoomHousekeepingStatus(propertyId, roomId, "DIRTY", issue));

        housekeeping.setMaintenanceIssue(issue);
        housekeeping.setMaintenanceSeverity(severity);
        if (markOutOfOrder || "CRITICAL".equalsIgnoreCase(severity)) {
            housekeeping.setStatus("OUT_OF_ORDER");
        }

        RoomHousekeepingStatus saved = housekeepingRepository.save(housekeeping);

        syncAliases(propertyId, roomId, null, saved.getStatus(), saved.getNotes(), saved.getAssignedTo(), saved.getTaskType(), saved.getPriority(), saved.getPhotoProofUrl(), saved.getChecklistJson());

        webSocketBroadcaster.broadcastHousekeeping("MAINTENANCE_LOGGED", saved);
        return saved;
    }

    @Operation(summary = "Resolve maintenance issue and return room to Turnover Required (DIRTY)")
    @PostMapping("/maintenance/resolve")
    public RoomHousekeepingStatus resolveMaintenance(@RequestBody Map<String, Object> payload) {
        Long propertyId = payload.get("propertyId") != null ? Long.valueOf(payload.get("propertyId").toString()) : 1L;
        String roomId = payload.get("roomId") != null ? payload.get("roomId").toString().trim() : "101";
        String resolutionNotes = payload.get("resolutionNotes") != null ? payload.get("resolutionNotes").toString() : "Maintenance resolved";

        Optional<RoomHousekeepingStatus> existing = housekeepingRepository.findByPropertyIdAndRoomId(propertyId, roomId);
        if (existing.isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }

        RoomHousekeepingStatus housekeeping = existing.get();
        housekeeping.setMaintenanceIssue(null);
        housekeeping.setMaintenanceSeverity(null);
        // After repair, room needs a cleaning turnover before guest check-in
        housekeeping.setStatus("DIRTY");
        housekeeping.setNotes("Maintenance resolved: " + resolutionNotes + " — Ready for turnover cleaning.");

        RoomHousekeepingStatus saved = housekeepingRepository.save(housekeeping);

        syncAliases(propertyId, roomId, null, "DIRTY", housekeeping.getNotes(), housekeeping.getAssignedTo(), housekeeping.getTaskType(), housekeeping.getPriority(), null, housekeeping.getChecklistJson());

        webSocketBroadcaster.broadcastHousekeeping("MAINTENANCE_RESOLVED", saved);
        return saved;
    }

    private void syncAliases(Long propertyId, String roomId, String roomName, String status, String notes, String assignedTo, String taskType, String priority, String photoUrl, String checklistJson) {
        String cleanName = roomId.split("—")[0].split("-")[0].trim();
        saveStatusForAlias(propertyId, cleanName, status, notes, assignedTo, taskType, priority, photoUrl, checklistJson);
        if (roomName != null && !roomName.isBlank()) {
            saveStatusForAlias(propertyId, roomName, status, notes, assignedTo, taskType, priority, photoUrl, checklistJson);
            saveStatusForAlias(propertyId, propertyId + "-" + roomName, status, notes, assignedTo, taskType, priority, photoUrl, checklistJson);
        }
        saveStatusForAlias(propertyId, propertyId + "-0", status, notes, assignedTo, taskType, priority, photoUrl, checklistJson);
        saveStatusForAlias(propertyId, propertyId + "-1", status, notes, assignedTo, taskType, priority, photoUrl, checklistJson);
        saveStatusForAlias(propertyId, propertyId + "-" + roomId, status, notes, assignedTo, taskType, priority, photoUrl, checklistJson);
        saveStatusForAlias(propertyId, propertyId + "-" + cleanName, status, notes, assignedTo, taskType, priority, photoUrl, checklistJson);
    }

    private void saveStatusForAlias(Long propId, String key, String status, String notes, String assignedTo, String taskType, String priority, String photoUrl, String checklistJson) {
        if (key == null || key.isBlank()) return;
        String trimmed = key.trim();
        Optional<RoomHousekeepingStatus> existing = housekeepingRepository.findByPropertyIdAndRoomId(propId, trimmed);
        RoomHousekeepingStatus hk = existing.orElseGet(() -> new RoomHousekeepingStatus(propId, trimmed, status, notes));
        hk.setStatus(status);
        if (notes != null) hk.setNotes(notes);
        if (assignedTo != null) hk.setAssignedTo(assignedTo);
        if (taskType != null) hk.setTaskType(taskType);
        if (priority != null) hk.setPriority(priority);

        if ("DIRTY".equalsIgnoreCase(status) || "OUT_OF_ORDER".equalsIgnoreCase(status)) {
            hk.setPhotoProofUrl(null);
            hk.setCleanedBy(null);
            hk.setLastCleanedAt(null);
            if (checklistJson == null && hk.getChecklistJson() != null) {
                hk.setChecklistJson(resetChecklistJson(hk.getChecklistJson()));
            } else if (checklistJson != null) {
                hk.setChecklistJson(resetChecklistJson(checklistJson));
            }
        } else {
            if (checklistJson != null) hk.setChecklistJson(checklistJson);
            if (photoUrl != null && !photoUrl.isBlank()) hk.setPhotoProofUrl(photoUrl);
            if ("CLEAN".equalsIgnoreCase(status) || "READY".equalsIgnoreCase(status) || "INSPECTED".equalsIgnoreCase(status)) {
                hk.setLastCleanedAt(LocalDateTime.now());
            }
        }
        housekeepingRepository.save(hk);
    }

    private String resetChecklistJson(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(json);
            if (root.isArray()) {
                com.fasterxml.jackson.databind.node.ArrayNode array = (com.fasterxml.jackson.databind.node.ArrayNode) root;
                for (com.fasterxml.jackson.databind.JsonNode node : array) {
                    if (node.isObject()) {
                        ((com.fasterxml.jackson.databind.node.ObjectNode) node).put("completed", false);
                    }
                }
                return mapper.writeValueAsString(array);
            }
        } catch (Exception ignored) {}
        return json;
    }

    @Operation(summary = "Caretaker submit photo proof and mark room clean")
    @PostMapping("/upload-proof")
    public RoomHousekeepingStatus uploadProofAndMarkClean(@RequestBody Map<String, Object> payload) {
        Long propertyId = payload.get("propertyId") != null ? Long.valueOf(payload.get("propertyId").toString()) : 1L;
        String roomId = payload.get("roomId") != null ? payload.get("roomId").toString().trim() : "101";
        String photoUrl = payload.get("photoUrl") != null ? payload.get("photoUrl").toString() : null;
        String cleanedBy = payload.get("cleanedBy") != null ? payload.get("cleanedBy").toString() : "Caretaker App";
        String notes = payload.get("notes") != null ? payload.get("notes").toString() : "Cleaned with verified photo proof";
        String checklistJson = payload.get("checklistJson") != null ? payload.get("checklistJson").toString() : null;

        if (photoUrl == null || photoUrl.isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST,
                "Photo Evidence Required: A room cannot be changed to 'Ready' / 'CLEAN' unless required photo evidence is provided."
            );
        }

        String cleanName = roomId.split("—")[0].split("-")[0].trim();

        RoomHousekeepingStatus primary = saveCleanStatusForAlias(propertyId, roomId, photoUrl, cleanedBy, notes, checklistJson);
        saveCleanStatusForAlias(propertyId, cleanName, photoUrl, cleanedBy, notes, checklistJson);
        saveCleanStatusForAlias(propertyId, propertyId + "-0", photoUrl, cleanedBy, notes, checklistJson);
        saveCleanStatusForAlias(propertyId, propertyId + "-1", photoUrl, cleanedBy, notes, checklistJson);
        saveCleanStatusForAlias(propertyId, propertyId + "-2", photoUrl, cleanedBy, notes, checklistJson);
        saveCleanStatusForAlias(propertyId, propertyId + "-" + roomId, photoUrl, cleanedBy, notes, checklistJson);
        saveCleanStatusForAlias(propertyId, propertyId + "-" + cleanName, photoUrl, cleanedBy, notes, checklistJson);

        webSocketBroadcaster.broadcastHousekeeping("PROOF_UPLOADED", primary);

        return primary;
    }

    private RoomHousekeepingStatus saveCleanStatusForAlias(Long propId, String key, String photoUrl, String cleanedBy, String notes, String checklistJson) {
        if (key == null || key.isBlank()) return null;
        String trimmed = key.trim();
        Optional<RoomHousekeepingStatus> existing = housekeepingRepository.findByPropertyIdAndRoomId(propId, trimmed);
        RoomHousekeepingStatus hk = existing.orElseGet(() -> new RoomHousekeepingStatus(propId, trimmed, "CLEAN", notes));
        hk.setStatus("CLEAN");
        hk.setPhotoProofUrl(photoUrl);
        hk.setCleanedBy(cleanedBy);
        hk.setNotes(notes);
        if (checklistJson != null) hk.setChecklistJson(checklistJson);
        hk.setLastCleanedAt(LocalDateTime.now());
        return housekeepingRepository.save(hk);
    }
}
