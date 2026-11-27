package stayops_api.service;

import org.springframework.stereotype.Service;

import stayops_api.dto.ReservationRequest;
import stayops_api.dto.ReservationResponse;
import stayops_api.entity.PaymentLog;
import stayops_api.entity.Property;
import stayops_api.entity.Reservation;
import stayops_api.entity.User;
import stayops_api.entity.RoomBlockedDate;
import stayops_api.entity.RoomHousekeepingStatus;
import stayops_api.repository.PaymentLogRepository;
import stayops_api.repository.PropertyRepository;
import stayops_api.repository.ReservationRepository;
import stayops_api.repository.RoomBlockedDateRepository;
import stayops_api.repository.RoomHousekeepingStatusRepository;
import stayops_api.repository.UserRepository;

import stayops_api.websocket.WebSocketBroadcaster;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final GuestProfileService guestProfileService;
    private final PaymentLogRepository paymentLogRepository;
    private final RoomBlockedDateRepository blockedDateRepository;
    private final RoomHousekeepingStatusRepository housekeepingRepository;
    private final WebSocketBroadcaster webSocketBroadcaster;
    private final TelegramBotService telegramBotService;

    public ReservationService(
            ReservationRepository reservationRepository,
            UserRepository userRepository,
            PropertyRepository propertyRepository,
            GuestProfileService guestProfileService,
            PaymentLogRepository paymentLogRepository,
            RoomBlockedDateRepository blockedDateRepository,
            RoomHousekeepingStatusRepository housekeepingRepository,
            WebSocketBroadcaster webSocketBroadcaster,
            TelegramBotService telegramBotService
    ) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.guestProfileService = guestProfileService;
        this.paymentLogRepository = paymentLogRepository;
        this.blockedDateRepository = blockedDateRepository;
        this.housekeepingRepository = housekeepingRepository;
        this.webSocketBroadcaster = webSocketBroadcaster;
        this.telegramBotService = telegramBotService;
    }


    // =========================================================
    // CREATE RESERVATION
    // =========================================================

    public Reservation createReservation(
            ReservationRequest request,
            Long authenticatedUserId
    ) {

        // =====================================================
        // FIND PROPERTY & PROPERTY OWNER
        // =====================================================

        Property property = propertyRepository
                .findById(request.getPropertyId() != null ? request.getPropertyId() : 34L)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Property not found"
                        )
                );

        User user = null;
        if (authenticatedUserId != null) {
            user = userRepository.findById(authenticatedUserId).orElse(null);
        }
        if (user == null && property != null) {
            user = property.getUser();
        }
        if (user == null) {
            user = userRepository.findAll().stream().findFirst().orElseThrow(() ->
                    new RuntimeException("No property owner user found in system")
            );
        }


        // =====================================================
        // CREATE RESERVATION
        // =====================================================

        Reservation reservation = new Reservation();

        reservation.setUser(user);
        reservation.setGuestName(request.getGuestName());
        reservation.setGuestEmail(request.getGuestEmail());
        reservation.setGuestPhone(request.getGuestPhone());
        reservation.setProperty(property);
        reservation.setBookingRef(generateBookingReference());
        reservation.setRoomUnit(request.getRoomUnit());
        reservation.setCheckIn(request.getCheckIn());
        reservation.setCheckInTime(request.getCheckInTime() != null ? request.getCheckInTime() : "14:00");
        reservation.setCheckOut(request.getCheckOut());
        reservation.setCheckOutTime(request.getCheckOutTime() != null ? request.getCheckOutTime() : "11:00");

        reservation.setGuestCount(request.getGuestCount() != null ? request.getGuestCount() : 1);
        reservation.setRoomBaseTotal(request.getRoomBaseTotal());
        reservation.setRoomSubtotal(request.getRoomSubtotal());
        reservation.setRoomServiceCharge(request.getRoomServiceCharge());
        reservation.setRoomGstAmount(request.getRoomGstAmount());
        reservation.setTaxSettingsSnapshot(request.getTaxSettingsSnapshot());
        reservation.setTotalAmount(request.getTotalAmount());
        reservation.setPaymentStatus(request.getPaymentStatus());
        reservation.setAmountPaid(request.getAmountPaid());
        reservation.setBookingSource(request.getBookingSource() != null ? request.getBookingSource() : "Direct Storefront");
        reservation.setNotes(request.getNotes());
        reservation.setRazorpayPaymentId(request.getRazorpayPaymentId());
        reservation.setIdType(request.getIdType());
        reservation.setIdNumber(request.getIdNumber());
        reservation.setIdDocumentUrl(request.getIdDocumentUrl());
        reservation.setIdDocuments(request.getIdDocuments());
        reservation.setGuestCity(request.getGuestCity());
        reservation.setGuestState(request.getGuestState());
        reservation.setStatus("Confirmed");

        // Save itemized allocated rooms (Multi-room or single room fallback)
        if (request.getAllocatedRooms() != null && !request.getAllocatedRooms().isEmpty()) {
            StringBuilder roomUnitStr = new StringBuilder();
            for (stayops_api.dto.ReservationRoomDTO rDto : request.getAllocatedRooms()) {
                stayops_api.entity.ReservationRoom rEntity = new stayops_api.entity.ReservationRoom(
                        rDto.getRoomUnit(),
                        rDto.getRoomTypeId(),
                        rDto.getRoomTypeName(),
                        rDto.getNightlyRate(),
                        rDto.getCapacity()
                );
                reservation.addAllocatedRoom(rEntity);
                if (rDto.getRoomUnit() != null && !rDto.getRoomUnit().isBlank()) {
                    if (roomUnitStr.length() > 0) roomUnitStr.append(", ");
                    roomUnitStr.append(rDto.getRoomUnit().trim());
                }
            }
            if ((request.getRoomUnit() == null || request.getRoomUnit().isBlank()) && roomUnitStr.length() > 0) {
                reservation.setRoomUnit(roomUnitStr.toString());
            }
        } else if (request.getRoomUnit() != null && !request.getRoomUnit().isBlank()) {
            stayops_api.entity.ReservationRoom rEntity = new stayops_api.entity.ReservationRoom(
                    request.getRoomUnit().trim(),
                    null,
                    null,
                    request.getRoomBaseTotal(),
                    request.getGuestCount()
            );
            reservation.addAllocatedRoom(rEntity);
        }

        // =====================================================
        // ROOM OCCUPANCY / DATE OVERLAP & BLOCKED DATES CHECK
        // =====================================================
        if (request.getRoomUnit() != null && !request.getRoomUnit().isBlank() && request.getCheckIn() != null && request.getCheckOut() != null) {
            String requestedRoom = request.getRoomUnit().trim();

            // 1. Check existing active reservations
            List<Reservation> existingReservations = reservationRepository.findByProperty_Id(property.getId());
            for (Reservation existing : existingReservations) {
                String status = existing.getStatus() != null ? existing.getStatus().toLowerCase() : "";
                if (status.contains("cancel") || status.contains("out") || status.contains("noshow") || status.contains("no_show")) {
                    continue;
                }

                boolean sameProperty = existing.getProperty() != null && existing.getProperty().getId().equals(request.getPropertyId());
                boolean sameRoom = existing.getRoomUnit() != null && existing.getRoomUnit().trim().equalsIgnoreCase(requestedRoom);

                if (sameProperty && sameRoom && existing.getCheckIn() != null && existing.getCheckOut() != null) {
                    boolean overlaps = request.getCheckIn().isBefore(existing.getCheckOut()) && request.getCheckOut().isAfter(existing.getCheckIn());
                    if (overlaps) {
                        throw new RuntimeException("Room unit '" + requestedRoom + "' is already occupied/reserved for the selected dates ("
                                + existing.getCheckIn() + " to " + existing.getCheckOut() + "). Please select another room or change dates.");
                    }
                }
            }

            // 2. Check blocked date ranges (Maintenance / Out of Order)
            List<RoomBlockedDate> blockedDates = blockedDateRepository.findByPropertyId(request.getPropertyId());
            for (RoomBlockedDate block : blockedDates) {
                boolean roomMatches = "all".equalsIgnoreCase(block.getRoomId()) || requestedRoom.equalsIgnoreCase(block.getRoomId());
                if (roomMatches && block.getStartDate() != null && block.getEndDate() != null) {
                    boolean overlaps = request.getCheckIn().isBefore(block.getEndDate()) && request.getCheckOut().isAfter(block.getStartDate());
                    if (overlaps) {
                        throw new RuntimeException("Room unit '" + requestedRoom + "' is blocked for (" + block.getCategory() 
                                + ": " + (block.getNotes() != null ? block.getNotes() : "Maintenance") + ") from " 
                                + block.getStartDate() + " to " + block.getEndDate() + ".");
                    }
                }
            }
        }

        // =====================================================
        // SAVE RESERVATION
        // =====================================================

        Reservation savedReservation = reservationRepository.save(reservation);

        // Auto-generate PaymentLog if initial advance payment was collected
        if (request.getAmountPaid() != null && request.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
            PaymentLog log = new PaymentLog(
                    user.getId(),
                    savedReservation.getBookingRef(),
                    savedReservation.getGuestName(),
                    savedReservation.getGuestPhone(),
                    "PAYMENT",
                    request.getAmountPaid(),
                    "MANUAL",
                    request.getBookingSource() != null ? request.getBookingSource() : "Cash / Walk-In",
                    null,
                    "MANUAL-PAY-" + savedReservation.getId(),
                    "CAPTURED",
                    "Initial advance payment at reservation creation"
            );
            paymentLogRepository.save(log);
        }

        // =====================================================
        // SYNC CRM GUEST PROFILE
        // =====================================================

        guestProfileService.syncGuestProfile(
                request.getGuestName(),
                request.getGuestEmail(),
                request.getGuestPhone(),
                request.getIdType(),
                request.getIdNumber(),
                request.getIdDocumentUrl(),
                request.getIdDocuments(),
                user
        );

        ReservationResponse resp = convertToResponse(savedReservation);
        webSocketBroadcaster.broadcastReservations("RESERVATION_CREATED", resp);

        try {
            telegramBotService.sendBookingNotification(property, savedReservation);
        } catch (Exception e) {
            // Keep reservation resilient if external Telegram API is temporarily unreachable
        }

        return savedReservation;
    }


    // =========================================================
    // GET RESERVATIONS FOR LOGGED-IN OWNER
    // =========================================================

    public List<ReservationResponse> getReservationsByUserId(
            Long userId
    ) {

        List<Reservation> reservations =
                reservationRepository.findByUser_Id(userId);

        return reservations.stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =========================================================
    // CONVERT ENTITY -> RESPONSE DTO
    // =========================================================

    private ReservationResponse convertToResponse(
            Reservation reservation
    ) {

        ReservationResponse resp = new ReservationResponse(
                reservation.getId(),
                reservation.getBookingRef(),
                reservation.getProperty() != null ? reservation.getProperty().getId() : null,
                reservation.getGuestName(),
                reservation.getGuestEmail(),
                reservation.getGuestPhone(),
                reservation.getProperty() != null ? reservation.getProperty().getName() : null,
                reservation.getRoomUnit(),
                reservation.getCheckIn(),
                reservation.getCheckInTime(),
                reservation.getCheckOut(),
                reservation.getCheckOutTime(),
                reservation.getGuestCount(),
                reservation.getTotalAmount(),
                reservation.getPaymentStatus(),
                reservation.getAmountPaid(),
                reservation.getBookingSource(),
                reservation.getNotes(),
                reservation.getRazorpayPaymentId(),
                reservation.getStatus(),
                reservation.getCreatedAt()
        );
        resp.setRoomBaseTotal(reservation.getRoomBaseTotal());
        resp.setRoomSubtotal(reservation.getRoomSubtotal());
        resp.setRoomServiceCharge(reservation.getRoomServiceCharge());
        resp.setRoomGstAmount(reservation.getRoomGstAmount());
        resp.setTaxSettingsSnapshot(reservation.getTaxSettingsSnapshot());
        resp.setIdType(reservation.getIdType());
        resp.setIdNumber(reservation.getIdNumber());
        resp.setIdDocumentUrl(reservation.getIdDocumentUrl());
        resp.setIdDocuments(reservation.getIdDocuments());
        resp.setGuestCity(reservation.getGuestCity());
        resp.setGuestState(reservation.getGuestState());

        if (reservation.getAllocatedRooms() != null && !reservation.getAllocatedRooms().isEmpty()) {
            java.util.List<stayops_api.dto.ReservationRoomDTO> rDtos = reservation.getAllocatedRooms().stream()
                    .map(r -> new stayops_api.dto.ReservationRoomDTO(
                            r.getId(),
                            r.getRoomUnit(),
                            r.getRoomTypeId(),
                            r.getRoomTypeName(),
                            r.getNightlyRate(),
                            r.getCapacity()
                    ))
                    .toList();
            resp.setAllocatedRooms(rDtos);
        }

        return resp;

    }


    // =========================================================
    // UPDATE STATUS
    // =========================================================

    public Reservation findReservationByIdentifier(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new RuntimeException("Reservation identifier is required");
        }
        String clean = identifier.trim();

        // 1. Try numeric primary key ID directly
        try {
            Long numericId = Long.parseLong(clean);
            java.util.Optional<Reservation> res = reservationRepository.findById(numericId);
            if (res.isPresent()) {
                return res.get();
            }
        } catch (NumberFormatException ignored) {}

        // 2. Try exact booking reference (e.g. "BK-008")
        java.util.Optional<Reservation> byRef = reservationRepository.findByBookingRef(clean);
        if (byRef.isPresent()) {
            return byRef.get();
        }

        // 3. Try with/without "BK-" prefix and padded variants
        if (!clean.toUpperCase().startsWith("BK-")) {
            java.util.Optional<Reservation> byPrefixedRef = reservationRepository.findByBookingRef("BK-" + clean);
            if (byPrefixedRef.isPresent()) {
                return byPrefixedRef.get();
            }
            try {
                long num = Long.parseLong(clean);
                String paddedRef = String.format("BK-%03d", num);
                java.util.Optional<Reservation> byPadded = reservationRepository.findByBookingRef(paddedRef);
                if (byPadded.isPresent()) {
                    return byPadded.get();
                }
            } catch (NumberFormatException ignored) {}
        } else {
            String suffix = clean.substring(3).trim();
            try {
                long num = Long.parseLong(suffix);
                String paddedRef = String.format("BK-%03d", num);
                java.util.Optional<Reservation> byPadded = reservationRepository.findByBookingRef(paddedRef);
                if (byPadded.isPresent()) {
                    return byPadded.get();
                }
            } catch (NumberFormatException ignored) {}
        }

        throw new RuntimeException("Reservation not found with ID or Reference: " + identifier);
    }

    public ReservationResponse updateReservationStatus(Long reservationId, String status, String reason, Long userId) {
        return updateReservationStatus(String.valueOf(reservationId), status, reason, null, null, null, null, null, null, userId);
    }

    public ReservationResponse updateReservationStatus(
            Long reservationId,
            String status,
            String reason,
            String idType,
            String idNumber,
            String idDocumentUrl,
            String idDocuments,
            String guestCity,
            String guestState,
            Long userId
    ) {
        return updateReservationStatus(String.valueOf(reservationId), status, reason, idType, idNumber, idDocumentUrl, idDocuments, guestCity, guestState, userId);
    }

    public ReservationResponse updateReservationStatus(
            String reservationIdentifier,
            String status,
            String reason,
            String idType,
            String idNumber,
            String idDocumentUrl,
            String idDocuments,
            String guestCity,
            String guestState,
            Long userId
    ) {
        Reservation reservation = findReservationByIdentifier(reservationIdentifier);

        if (!reservation.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Reservation belongs to another user");
        }

        reservation.setStatus(status);
        if (reason != null && !reason.isBlank()) {
            String existingNotes = reservation.getNotes();
            if (existingNotes != null && !existingNotes.isBlank()) {
                reservation.setNotes(existingNotes + " | " + reason);
            } else {
                reservation.setNotes(reason);
            }
        }

        if (idType != null && !idType.isBlank()) {
            reservation.setIdType(idType);
        }
        if (idNumber != null && !idNumber.isBlank()) {
            reservation.setIdNumber(idNumber);
        }
        if (idDocumentUrl != null && !idDocumentUrl.isBlank()) {
            reservation.setIdDocumentUrl(idDocumentUrl);
        }
        if (idDocuments != null && !idDocuments.isBlank()) {
            reservation.setIdDocuments(idDocuments);
        }
        if (guestCity != null && !guestCity.isBlank()) {
            reservation.setGuestCity(guestCity);
        }
        if (guestState != null && !guestState.isBlank()) {
            reservation.setGuestState(guestState);
        }

        Reservation saved = reservationRepository.save(reservation);

        // Sync ID and guest profile to Guest CRM if phone is available
        if (saved.getGuestPhone() != null && !saved.getGuestPhone().isBlank() && saved.getUser() != null) {
            guestProfileService.syncGuestProfile(
                    saved.getGuestName(),
                    saved.getGuestEmail(),
                    saved.getGuestPhone(),
                    saved.getIdType(),
                    saved.getIdNumber(),
                    saved.getIdDocumentUrl(),
                    saved.getIdDocuments(),
                    saved.getUser()
            );
        }

        // AUTO-DIRTY TURNOVER TRIGGER ON CHECK-OUT
        String lowerStatus = status != null ? status.toLowerCase() : "";
        if (lowerStatus.contains("checkout") || lowerStatus.contains("check_out") || lowerStatus.contains("checked out") || lowerStatus.contains("depart") || lowerStatus.contains("completed")) {
            if (saved.getRoomUnit() != null && !saved.getRoomUnit().isBlank()) {
                Long propId = saved.getProperty() != null ? saved.getProperty().getId() : 1L;
                String roomNum = saved.getRoomUnit().trim();
                String cleanName = roomNum.split("—")[0].split("-")[0].trim();

                markRoomDirtyAlias(propId, roomNum, saved);
                markRoomDirtyAlias(propId, cleanName, saved);
                markRoomDirtyAlias(propId, propId + "-0", saved);
                markRoomDirtyAlias(propId, propId + "-1", saved);
                markRoomDirtyAlias(propId, propId + "-2", saved);
                markRoomDirtyAlias(propId, propId + "-" + roomNum, saved);
                markRoomDirtyAlias(propId, propId + "-" + cleanName, saved);

                // Find and reset any existing alias records for this property
                try {
                    List<RoomHousekeepingStatus> allPropHk = housekeepingRepository.findByPropertyId(propId);
                    for (RoomHousekeepingStatus s : allPropHk) {
                        String rId = s.getRoomId();
                        if (rId != null && (rId.equalsIgnoreCase(roomNum) || rId.equalsIgnoreCase(cleanName) || rId.toLowerCase().contains(cleanName.toLowerCase()))) {
                            markRoomDirtyAlias(propId, rId, saved);
                        }
                    }
                } catch (Exception ignored) {}
            }
        }

        ReservationResponse resp = convertToResponse(saved);
        webSocketBroadcaster.broadcastReservations("RESERVATION_UPDATED", resp);
        return resp;
    }

    public void deleteReservation(Long id, Long userId) {
        deleteReservation(String.valueOf(id), userId);
    }

    public void deleteReservation(String identifier, Long userId) {
        Reservation reservation = findReservationByIdentifier(identifier);

        if (!reservation.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Reservation belongs to another user");
        }

        reservationRepository.delete(reservation);

        webSocketBroadcaster.broadcastReservations("RESERVATION_DELETED", java.util.Map.of(
                "id", reservation.getId(),
                "bookingRef", reservation.getBookingRef() != null ? reservation.getBookingRef() : "BK-" + reservation.getId(),
                "guestName", reservation.getGuestName() != null ? reservation.getGuestName() : "Guest"
        ));
    }


    // =========================================================
    // UPDATE PAYMENT & FOLIO (WITH EARLY DEPARTURE SUPPORT)
    // =========================================================

    public ReservationResponse updateReservationPayment(
            Long reservationId,
            BigDecimal amountPaid,
            BigDecimal totalAmount,
            String paymentStatus,
            String checkOut,
            String notes,
            Long userId
    ) {
        return updateReservationPayment(String.valueOf(reservationId), amountPaid, totalAmount, paymentStatus, checkOut, notes, userId);
    }

    public ReservationResponse updateReservationPayment(
            String reservationIdentifier,
            BigDecimal amountPaid,
            BigDecimal totalAmount,
            String paymentStatus,
            String checkOut,
            String notes,
            Long userId
    ) {
        Reservation reservation = findReservationByIdentifier(reservationIdentifier);

        if (!reservation.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Reservation belongs to another user");
        }

        BigDecimal oldPaid = reservation.getAmountPaid() != null ? reservation.getAmountPaid() : BigDecimal.ZERO;

        if (amountPaid != null) {
            reservation.setAmountPaid(amountPaid);
        }
        if (totalAmount != null) {
            reservation.setTotalAmount(totalAmount);
        }
        if (paymentStatus != null && !paymentStatus.isBlank()) {
            reservation.setPaymentStatus(paymentStatus);
        }
        if (checkOut != null && !checkOut.isBlank()) {
            try {
                reservation.setCheckOut(java.time.LocalDate.parse(checkOut));
            } catch (Exception ignored) {}
        }
        if (notes != null && !notes.isBlank()) {
            reservation.setNotes(notes);
        }

        Reservation saved = reservationRepository.save(reservation);

        // Auto-generate PaymentLog for incremental payment collected
        if (amountPaid != null) {
            BigDecimal deltaPaid = amountPaid.subtract(oldPaid);
            if (deltaPaid.compareTo(BigDecimal.ZERO) > 0) {
                PaymentLog log = new PaymentLog(
                        userId,
                        saved.getBookingRef(),
                        saved.getGuestName(),
                        saved.getGuestPhone(),
                        "PAYMENT",
                        deltaPaid,
                        "MANUAL",
                        paymentStatus != null ? paymentStatus : "Cash/Frontdesk",
                        null,
                        "MANUAL-PAY-" + saved.getId() + "-" + System.currentTimeMillis(),
                        "CAPTURED",
                        notes != null ? notes : "Frontdesk payment collected"
                );
                paymentLogRepository.save(log);
            }
        }

        ReservationResponse resp = convertToResponse(saved);
        webSocketBroadcaster.broadcastReservations("PAYMENT_UPDATED", resp);
        return resp;
    }


    private void markRoomDirtyAlias(Long propId, String key, Reservation saved) {
        if (key == null || key.isBlank()) return;
        String trimmedKey = key.trim();
        Optional<RoomHousekeepingStatus> existing = housekeepingRepository.findByPropertyIdAndRoomId(propId, trimmedKey);
        RoomHousekeepingStatus hk = existing.orElseGet(() -> new RoomHousekeepingStatus(propId, trimmedKey, "DIRTY", "Automatic Turnover: Guest Checked Out"));
        hk.setStatus("DIRTY");
        hk.setPhotoProofUrl(null);
        hk.setCleanedBy(null);
        hk.setLastCleanedAt(null);
        hk.setNotes("Turnover required: Checked-out guest (" + saved.getGuestName() + " - " + saved.getBookingRef() + ").");
        if (hk.getChecklistJson() != null) {
            hk.setChecklistJson(hk.getChecklistJson().replaceAll("\"completed\"\\s*:\\s*true", "\"completed\":false"));
        }
        RoomHousekeepingStatus savedHk = housekeepingRepository.save(hk);
        webSocketBroadcaster.broadcastHousekeeping("STATUS_UPDATED", savedHk);
    }

    // =========================================================
    // BOOKING REFERENCE
    // =========================================================

    private synchronized String generateBookingReference() {
        long nextNumber = reservationRepository.count() + 1;
        String candidate = String.format("BK-%03d", nextNumber);
        while (reservationRepository.existsByBookingRef(candidate)) {
            nextNumber++;
            candidate = String.format("BK-%03d", nextNumber);
        }
        return candidate;
    }
}