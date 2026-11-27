package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import stayops_api.dto.ReservationRequest;
import stayops_api.dto.ReservationResponse;
import stayops_api.entity.Reservation;
import stayops_api.service.ReservationService;

import java.util.List;

@Tag(name = "3. Front Desk & Reservations", description = "Room Calendar, Booking Creation, Status Updates, and Guest Stays")
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService
    ) {
        this.reservationService = reservationService;
    }


    // =========================================================
    // CREATE
    // =========================================================

    @Operation(summary = "Create a new Front-Desk Reservation")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reservation createReservation(
            @RequestBody ReservationRequest request,
            Authentication authentication
    ) {
        Long userId = null;
        if (authentication != null && authentication.isAuthenticated() && authentication.getName() != null) {
            try {
                userId = Long.valueOf(authentication.getName());
            } catch (Exception ignored) {}
        }

        return reservationService.createReservation(
                request,
                userId
        );
    }


    // =========================================================
    // GET MY RESERVATIONS
    // =========================================================

    @GetMapping
    public List<ReservationResponse> getMyReservations(
            Authentication authentication
    ) {

        Long userId = getAuthenticatedUserId(authentication);

        return reservationService.getReservationsByUserId(userId);
    }


    // =========================================================
    // UPDATE RESERVATION STATUS (CHECK-IN / CHECK-OUT / NO-SHOW)
    // =========================================================

    @PutMapping("/{id}/status")
    public ReservationResponse updateReservationStatus(
            @PathVariable String id,
            @RequestBody java.util.Map<String, String> body,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        String status = body.get("status");
        String reason = body.get("reason");
        String idType = body.get("idType");
        String idNumber = body.get("idNumber");
        String idDocumentUrl = body.get("idDocumentUrl");
        String idDocuments = body.get("idDocuments");
        String guestCity = body.get("guestCity");
        String guestState = body.get("guestState");
        return reservationService.updateReservationStatus(id, status, reason, idType, idNumber, idDocumentUrl, idDocuments, guestCity, guestState, userId);
    }


    // =========================================================
    // UPDATE RESERVATION PAYMENT
    // =========================================================

    @PutMapping("/{id}/payment")
    public ReservationResponse updateReservationPayment(
            @PathVariable String id,
            @RequestBody java.util.Map<String, Object> body,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        java.math.BigDecimal amountPaid = body.get("amountPaid") != null ? new java.math.BigDecimal(body.get("amountPaid").toString()) : null;
        java.math.BigDecimal totalAmount = body.get("totalAmount") != null ? new java.math.BigDecimal(body.get("totalAmount").toString()) : null;
        String paymentStatus = body.get("paymentStatus") != null ? body.get("paymentStatus").toString() : null;
        String checkOut = body.get("checkOut") != null ? body.get("checkOut").toString() : null;
        String notes = body.get("notes") != null ? body.get("notes").toString() : null;

        return reservationService.updateReservationPayment(id, amountPaid, totalAmount, paymentStatus, checkOut, notes, userId);
    }

    // =========================================================
    // DELETE RESERVATION
    // =========================================================

    @DeleteMapping("/{id}")
    public java.util.Map<String, String> deleteReservation(
            @PathVariable String id,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        reservationService.deleteReservation(id, userId);
        return java.util.Map.of("message", "Reservation deleted successfully");
    }


    // =========================================================
    // GET AUTHENTICATED USER ID
    // =========================================================

    private Long getAuthenticatedUserId(
            Authentication authentication
    ) {

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getName() == null) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }

        try {

            return Long.valueOf(
                    authentication.getName()
            );

        } catch (NumberFormatException e) {

            throw new RuntimeException(
                    "Invalid authenticated user ID"
            );
        }
    }
}