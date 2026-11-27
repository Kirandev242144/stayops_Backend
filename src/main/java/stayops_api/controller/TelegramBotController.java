package stayops_api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stayops_api.dto.TelegramConnectRequest;
import stayops_api.dto.TelegramPreferencesRequest;
import stayops_api.dto.TelegramStatusResponse;
import stayops_api.service.TelegramBotService;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class TelegramBotController {

    private final TelegramBotService telegramBotService;

    public TelegramBotController(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    // ==========================================
    // OWNER SETTINGS ENDPOINTS (Authenticated)
    // ==========================================

    @GetMapping("/api/properties/{propertyId}/telegram/status")
    public ResponseEntity<TelegramStatusResponse> getBotStatus(
            @PathVariable Long propertyId,
            org.springframework.security.core.Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        TelegramStatusResponse status = telegramBotService.getBotStatus(propertyId, userId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/api/properties/{propertyId}/telegram/connect")
    public ResponseEntity<TelegramStatusResponse> connectBot(
            @PathVariable Long propertyId,
            @Valid @RequestBody TelegramConnectRequest request,
            org.springframework.security.core.Authentication authentication,
            HttpServletRequest httpServletRequest
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        String scheme = httpServletRequest.getScheme();
        String serverName = httpServletRequest.getServerName();
        int serverPort = httpServletRequest.getServerPort();
        String backendBaseUrl = scheme + "://" + serverName + (serverPort == 80 || serverPort == 443 ? "" : ":" + serverPort);

        TelegramStatusResponse status = telegramBotService.connectBot(propertyId, userId, request.getBotToken(), backendBaseUrl);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/api/properties/{propertyId}/telegram/disconnect")
    public ResponseEntity<Map<String, Object>> disconnectBot(
            @PathVariable Long propertyId,
            org.springframework.security.core.Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        telegramBotService.disconnectBot(propertyId, userId);
        return ResponseEntity.ok(Map.of("success", true, "message", "Telegram bot disconnected successfully"));
    }

    @PutMapping("/api/properties/{propertyId}/telegram/preferences")
    public ResponseEntity<TelegramStatusResponse> updatePreferences(
            @PathVariable Long propertyId,
            @RequestBody TelegramPreferencesRequest request,
            org.springframework.security.core.Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        TelegramStatusResponse status = telegramBotService.updatePreferences(propertyId, userId, request);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/api/properties/{propertyId}/telegram/test-alert")
    public ResponseEntity<Map<String, Object>> sendTestAlert(
            @PathVariable Long propertyId,
            org.springframework.security.core.Authentication authentication
    ) {
        Long userId = getAuthenticatedUserId(authentication);
        telegramBotService.sendTestAlert(propertyId, userId);
        return ResponseEntity.ok(Map.of("success", true, "message", "Test alert dispatched to owner Telegram chat"));
    }

    private Long getAuthenticatedUserId(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "User is not authenticated"
            );
        }
        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException e) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid authentication principal"
            );
        }
    }

    // ==========================================
    // PUBLIC TELEGRAM WEBHOOK (From api.telegram.org)
    // ==========================================

    @PostMapping("/api/public/telegram/webhook/{propertyId}")
    public ResponseEntity<String> handleTelegramWebhook(
            @PathVariable Long propertyId,
            @RequestHeader(value = "X-Telegram-Bot-Api-Secret-Token", required = false) String secretToken,
            @RequestBody Map<String, Object> update
    ) {
        telegramBotService.handleIncomingUpdate(propertyId, secretToken, update);
        return ResponseEntity.ok("OK");
    }
}
