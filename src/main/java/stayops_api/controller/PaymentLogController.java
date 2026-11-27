package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import stayops_api.entity.PaymentLog;
import stayops_api.entity.Reservation;
import stayops_api.repository.PaymentLogRepository;
import stayops_api.repository.ReservationRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "10. Payment Gateways & Logs", description = "Razorpay payment integration, webhooks, and transaction audit logs")
@RestController
@RequestMapping("/api/payments/logs")
@CrossOrigin(origins = "*")
public class PaymentLogController {

    @Autowired
    private PaymentLogRepository paymentLogRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Operation(summary = "Get all payment audit logs for current authenticated property owner")
    @GetMapping
    public ResponseEntity<List<PaymentLog>> getPaymentLogs(Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);

        // Synchronize and ensure refund records exist for any cancelled/refunded reservations
        syncReservationLogs(userId);

        List<PaymentLog> dbLogs = paymentLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return ResponseEntity.ok(dbLogs);
    }

    @Operation(summary = "Post a manual payment or refund log entry")
    @PostMapping
    public ResponseEntity<PaymentLog> createPaymentLog(
            @RequestBody PaymentLog log,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        log.setUserId(userId);
        PaymentLog saved = paymentLogRepository.save(log);
        return ResponseEntity.ok(saved);
    }

    private void syncReservationLogs(Long userId) {
        List<Reservation> reservations = reservationRepository.findByUser_Id(userId);
        List<PaymentLog> existingLogs = paymentLogRepository.findByUserIdOrderByCreatedAtDesc(userId);

        // Fix any existing manual refund logs that accidentally stored a synthetic razorpay refundId
        for (PaymentLog existing : existingLogs) {
            if ("REFUND".equalsIgnoreCase(existing.getType())) {
                String notes = existing.getNotes() != null ? existing.getNotes() : "";
                boolean isRazorpay = notes.contains("Razorpay Online Refund") || notes.contains("Razorpay Gateway Auto-Refund");
                if (!isRazorpay && existing.getRefundId() != null) {
                    existing.setRefundId(null);
                    paymentLogRepository.save(existing);
                }
            }
        }

        List<PaymentLog> newLogs = new ArrayList<>();

        for (Reservation res : reservations) {
            String bRef = res.getBookingRef() != null ? res.getBookingRef() : "BK-" + res.getId();
            // Calculate sum of existing captured PAYMENT log amounts for this booking
            BigDecimal existingPaymentTotal = existingLogs.stream()
                    .filter(l -> "PAYMENT".equalsIgnoreCase(l.getType()) && bRef.equalsIgnoreCase(l.getBookingRef()) && "CAPTURED".equalsIgnoreCase(l.getStatus()))
                    .map(PaymentLog::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal resPaid = res.getAmountPaid() != null ? res.getAmountPaid() : BigDecimal.ZERO;

            if (resPaid.compareTo(existingPaymentTotal) > 0) {
                BigDecimal unloggedAmount = resPaid.subtract(existingPaymentTotal);
                String pId = res.getRazorpayPaymentId() != null ? res.getRazorpayPaymentId() : "MANUAL-PAY-" + res.getId() + (existingPaymentTotal.compareTo(BigDecimal.ZERO) > 0 ? "-BAL" : "");
                String gateway = res.getRazorpayPaymentId() != null ? "RAZORPAY" : "MANUAL";
                String method = res.getRazorpayPaymentId() != null ? "Online (Razorpay)" : (res.getBookingSource() != null ? res.getBookingSource() : "Cash / UPI");

                PaymentLog payLog = new PaymentLog(
                        userId,
                        bRef,
                        res.getGuestName(),
                        res.getGuestPhone(),
                        "PAYMENT",
                        unloggedAmount,
                        gateway,
                        method,
                        null,
                        pId,
                        "CAPTURED",
                        existingPaymentTotal.compareTo(BigDecimal.ZERO) > 0
                                ? "Balance payment collected for booking " + bRef
                                : "Original payment recorded for booking " + bRef
                );
                newLogs.add(payLog);
            }

            // Check if REFUND log exists when reservation is refunded or cancelled
            boolean isRefunded = "Refunded".equalsIgnoreCase(res.getPaymentStatus()) ||
                    "Partial Refund".equalsIgnoreCase(res.getPaymentStatus()) ||
                    "cancelled".equalsIgnoreCase(res.getStatus());

            if (isRefunded) {
                boolean hasRefundLog = existingLogs.stream().anyMatch(
                        l -> "REFUND".equalsIgnoreCase(l.getType()) && bRef.equalsIgnoreCase(l.getBookingRef())
                );

                if (!hasRefundLog) {
                    String pId = res.getRazorpayPaymentId() != null ? res.getRazorpayPaymentId() : "MANUAL-PAY-" + res.getId();
                    String gateway = res.getRazorpayPaymentId() != null ? "RAZORPAY" : "MANUAL";
                    String notes = res.getNotes() != null ? res.getNotes() : "Reservation cancelled and refunded.";

                    boolean isRazorpayRefund = notes.contains("Razorpay Online Refund") || notes.contains("Razorpay Gateway Auto-Refund");
                    
                    String refundMethod = "Manual (Cash / UPI / Bank)";
                    if (isRazorpayRefund) {
                        refundMethod = "Razorpay Online Refund";
                    } else if (notes.contains("Refund:")) {
                        int idx = notes.indexOf("Refund:");
                        String sub = notes.substring(idx).trim();
                        int startParen = sub.indexOf("(");
                        int endParen = sub.lastIndexOf(")");
                        if (startParen != -1 && endParen != -1 && endParen > startParen) {
                            refundMethod = sub.substring(startParen + 1, endParen).trim();
                        }
                    }

                    // Only assign a Gateway Refund ID if actually processed via Razorpay API
                    String refundId = isRazorpayRefund ? "rfnd_auto_" + res.getId() : null;

                    PaymentLog refundLog = new PaymentLog(
                            userId,
                            bRef,
                            res.getGuestName(),
                            res.getGuestPhone(),
                            "REFUND",
                            resPaid,
                            gateway,
                            gateway.equals("RAZORPAY") ? "Online (Razorpay)" : "Manual Settlement",
                            null,
                            pId,
                            "REFUNDED",
                            notes
                    );
                    refundLog.setRefundId(refundId);
                    refundLog.setRefundMethod(refundMethod);

                    newLogs.add(refundLog);
                }
            }
        }

        if (!newLogs.isEmpty()) {
            paymentLogRepository.saveAll(newLogs);
        }
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
