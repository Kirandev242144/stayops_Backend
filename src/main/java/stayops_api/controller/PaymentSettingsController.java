package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import stayops_api.entity.PaymentGatewayCredentials;
import stayops_api.entity.User;
import stayops_api.repository.PaymentGatewayCredentialsRepository;
import stayops_api.repository.UserRepository;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "10. Payment Gateways & Logs", description = "Razorpay payment integration, webhooks, and transaction audit logs")
@RestController
@RequestMapping("/api/settings/payment")
@CrossOrigin(origins = "*")
public class PaymentSettingsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentGatewayCredentialsRepository credentialsRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPaymentSettings(Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        PaymentGatewayCredentials creds = credentialsRepository.findByUserIdAndGatewayName(userId, "RAZORPAY")
                .orElse(null);

        String keyId = (creds != null && creds.getKeyId() != null) ? creds.getKeyId() : "";
        String keySecret = (creds != null && creds.getKeySecret() != null) ? creds.getKeySecret() : "";
        String webhookSecret = (creds != null && creds.getWebhookSecret() != null) ? creds.getWebhookSecret() : "";

        boolean isConnected = false;
        if (!keyId.isEmpty() && !keySecret.isEmpty()) {
            isConnected = validateRazorpayKeys(keyId, keySecret);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("keyId", keyId);
        response.put("keySecret", keySecret.isEmpty() ? "" : (keySecret.length() > 6 ? keySecret.substring(0, 4) + "••••••••" : "••••••••"));
        response.put("webhookSecret", webhookSecret.isEmpty() ? "" : (webhookSecret.length() > 6 ? webhookSecret.substring(0, 4) + "••••••••" : "••••••••"));
        response.put("connectionStatus", isConnected ? "connected" : "not-connected");

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> savePaymentSettings(
            @RequestBody Map<String, String> payload,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PaymentGatewayCredentials creds = credentialsRepository.findByUserIdAndGatewayName(userId, "RAZORPAY")
                .orElse(new PaymentGatewayCredentials(user, "RAZORPAY", "", "", ""));

        String keyId = payload.getOrDefault("keyId", "").trim();
        String keySecret = payload.getOrDefault("keySecret", "").trim();
        String webhookSecret = payload.getOrDefault("webhookSecret", "").trim();

        // Use existing stored secret if user did not modify masked secret
        if (keySecret.contains("••••") && creds.getKeySecret() != null) {
            keySecret = creds.getKeySecret();
        }
        if (webhookSecret.contains("••••") && creds.getWebhookSecret() != null) {
            webhookSecret = creds.getWebhookSecret();
        }

        // Test credentials live against Razorpay API before saving
        boolean isValid = validateRazorpayKeys(keyId, keySecret);
        if (!isValid) {
            creds.setKeyId(null);
            creds.setKeySecret(null);
            creds.setConnectionStatus("not-connected");
            credentialsRepository.save(creds);

            Map<String, String> err = new HashMap<>();
            err.put("message", "Cannot save: Invalid Razorpay Key ID or Key Secret. Key must start with rzp_test_ / rzp_live_ and pass API authentication.");
            err.put("connectionStatus", "not-connected");
            return ResponseEntity.badRequest().body(err);
        }

        creds.setUser(user);
        creds.setGatewayName("RAZORPAY");
        creds.setKeyId(keyId);
        creds.setKeySecret(keySecret);
        creds.setWebhookSecret(webhookSecret);
        creds.setConnectionStatus("connected");

        credentialsRepository.save(creds);

        Map<String, String> res = new HashMap<>();
        res.put("message", "🎉 Razorpay credentials verified, encrypted, and saved successfully!");
        res.put("connectionStatus", "connected");
        return ResponseEntity.ok(res);
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> testPaymentConnection(
            @RequestBody Map<String, String> payload,
            Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        PaymentGatewayCredentials creds = credentialsRepository.findByUserIdAndGatewayName(userId, "RAZORPAY").orElse(null);

        String keyId = payload.getOrDefault("keyId", "").trim();
        String keySecret = payload.getOrDefault("keySecret", "").trim();

        if (keyId.isEmpty() && creds != null && creds.getKeyId() != null) {
            keyId = creds.getKeyId();
        }
        if ((keySecret.isEmpty() || keySecret.contains("••••")) && creds != null && creds.getKeySecret() != null) {
            keySecret = creds.getKeySecret();
        }

        boolean isValid = validateRazorpayKeys(keyId, keySecret);
        Map<String, Object> res = new HashMap<>();
        if (isValid) {
            res.put("success", true);
            res.put("message", "Razorpay Test API Connection Successful! Key ID & Key Secret verified.");
            return ResponseEntity.ok(res);
        } else {
            res.put("success", false);
            res.put("message", "Razorpay API Auth Failed: Key must start with rzp_test_ or rzp_live_ and be a valid Razorpay API Key.");
            return ResponseEntity.badRequest().body(res);
        }
    }

    private boolean validateRazorpayKeys(String keyId, String keySecret) {
        if (keyId == null || keyId.trim().isEmpty() || keySecret == null || keySecret.trim().isEmpty()) {
            return false;
        }
        String cleanKey = keyId.trim();
        if (!cleanKey.startsWith("rzp_test_") && !cleanKey.startsWith("rzp_live_")) {
            return false;
        }
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            String authStr = cleanKey + ":" + keySecret.trim();
            String base64Auth = Base64.getEncoder().encodeToString(authStr.getBytes());
            headers.set("Authorization", "Basic " + base64Auth);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.razorpay.com/v1/payments?count=1",
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
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
