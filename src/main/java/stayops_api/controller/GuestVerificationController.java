package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stayops_api.websocket.WebSocketBroadcaster;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller for Amazon-style Guest Mobile ID Verification sessions.
 * Generates temporary one-time upload tokens, serves mobile upload pages,
 * and broadcasts real-time WebSocket events back to the front-desk desktop.
 */
@RestController
@RequestMapping("/api/guest-verification")
@Tag(name = "Guest Mobile Verification", description = "Secure one-time mobile ID upload sessions")
public class GuestVerificationController {

    private static final Logger logger = LoggerFactory.getLogger(GuestVerificationController.class);

    private final WebSocketBroadcaster webSocketBroadcaster;

    // In-memory thread-safe store for temporary 30-min verification sessions
    private final Map<String, VerificationSession> sessions = new ConcurrentHashMap<>();

    public GuestVerificationController(WebSocketBroadcaster webSocketBroadcaster) {
        this.webSocketBroadcaster = webSocketBroadcaster;
    }

    public static class VerificationSession {
        private String token;
        private String guestName;
        private String guestPhone;
        private String propertyName;
        private String propertyId;
        private Instant createdAt;
        private Instant expiresAt;
        private String status; // PENDING, COMPLETED, EXPIRED
        private Map<String, Object> submissionData;

        public VerificationSession(String token, String guestName, String guestPhone, String propertyName, String propertyId) {
            this.token = token;
            this.guestName = guestName;
            this.guestPhone = guestPhone;
            this.propertyName = propertyName;
            this.propertyId = propertyId;
            this.createdAt = Instant.now();
            this.expiresAt = Instant.now().plusSeconds(86400); // 24 hours
            this.status = "PENDING";
            this.submissionData = new HashMap<>();
        }

        public boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }

        // Getters & Setters
        public String getToken() { return token; }
        public String getGuestName() { return guestName; }
        public String getGuestPhone() { return guestPhone; }
        public String getPropertyName() { return propertyName; }
        public String getPropertyId() { return propertyId; }
        public Instant getCreatedAt() { return createdAt; }
        public Instant getExpiresAt() { return expiresAt; }
        public String getStatus() { return isExpired() ? "EXPIRED" : status; }
        public void setStatus(String status) { this.status = status; }
        public Map<String, Object> getSubmissionData() { return submissionData; }
        public void setSubmissionData(Map<String, Object> data) { this.submissionData = data; }
    }

    @Operation(summary = "Create a temporary mobile ID verification session token")
    @PostMapping("/session")
    public ResponseEntity<?> createSession(@RequestBody Map<String, String> request) {
        String guestName = request.getOrDefault("guestName", "Guest");
        String guestPhone = request.getOrDefault("guestPhone", "");
        String propertyName = request.getOrDefault("propertyName", "StayOps Property");
        String propertyId = request.getOrDefault("propertyId", "");

        // Generate clean URL-safe token
        String token = "tok_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        VerificationSession session = new VerificationSession(token, guestName, guestPhone, propertyName, propertyId);
        sessions.put(token, session);

        logger.info("[GuestVerification] Created mobile upload session {} for guest: {} (phone: {})", token, guestName, guestPhone);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("guestName", session.getGuestName());
        response.put("guestPhone", session.getGuestPhone());
        response.put("propertyName", session.getPropertyName());
        response.put("expiresAt", session.getExpiresAt().toString());
        response.put("status", session.getStatus());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Validate a verification token and get session metadata for mobile")
    @GetMapping("/session/{token}")
    public ResponseEntity<?> getSession(@PathVariable String token) {
        VerificationSession session = sessions.get(token);

        if (session == null) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Verification session not found or link has expired.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }

        if (session.isExpired()) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "This verification link has expired for security reasons. Please ask front desk for a new link.");
            return ResponseEntity.status(HttpStatus.GONE).body(err);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("token", session.getToken());
        response.put("guestName", session.getGuestName());
        response.put("guestPhone", session.getGuestPhone());
        response.put("propertyName", session.getPropertyName());
        response.put("expiresAt", session.getExpiresAt().toString());
        response.put("status", session.getStatus());
        response.put("isCompleted", "COMPLETED".equals(session.getStatus()));

        // Return uploaded ID document data (urls, idType, idNumber) so front desk polling gets them
        if (session.getSubmissionData() != null) {
            response.putAll(session.getSubmissionData());
        }

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Submit uploaded guest ID from mobile and broadcast to front desk")
    @PostMapping("/submit")
    public ResponseEntity<?> submitVerification(@RequestBody Map<String, Object> payload) {
        String token = (String) payload.get("token");
        if (token == null || !sessions.containsKey(token)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired session token."));
        }

        VerificationSession session = sessions.get(token);
        if (session.isExpired()) {
            return ResponseEntity.status(HttpStatus.GONE).body(Map.of("error", "Verification link expired."));
        }

        session.setStatus("COMPLETED");
        session.setSubmissionData(payload);

        logger.info("[GuestVerification] ID uploaded successfully for session {} by guest: {}", token, session.getGuestName());

        // Prepare live broadcast event payload for front desk
        Map<String, Object> broadcastPayload = new HashMap<>(payload);
        broadcastPayload.put("token", token);
        broadcastPayload.put("guestName", session.getGuestName());
        broadcastPayload.put("guestPhone", session.getGuestPhone());
        broadcastPayload.put("timestamp", Instant.now().toString());
        broadcastPayload.put("isCompleted", true);

        // Broadcast to WebSocket clients subscribed to GUEST_VERIFICATION, FRONTDESK, and RESERVATIONS
        webSocketBroadcaster.broadcastGuestVerification("GUEST_ID_UPLOADED", broadcastPayload);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "ID submitted successfully. You are all set!");
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}
