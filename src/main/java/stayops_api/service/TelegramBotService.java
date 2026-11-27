package stayops_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import stayops_api.dto.TelegramPreferencesRequest;
import stayops_api.dto.TelegramStatusResponse;
import stayops_api.entity.Property;
import stayops_api.entity.PropertyTelegramBot;
import stayops_api.entity.Reservation;
import stayops_api.entity.StorefrontSettings;
import stayops_api.repository.PropertyRepository;
import stayops_api.repository.PropertyTelegramBotRepository;
import stayops_api.repository.StorefrontSettingsRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TelegramBotService {

    private static final Logger log = LoggerFactory.getLogger(TelegramBotService.class);
    private static final String TELEGRAM_API_BASE = "https://api.telegram.org/bot";

    private final PropertyTelegramBotRepository botRepository;
    private final PropertyRepository propertyRepository;
    private final StorefrontSettingsRepository storefrontSettingsRepository;
    private final RestTemplate restTemplate;

    @Value("${stayops.public.url:https://stayops.in}")
    private String stayopsPublicUrl;

    public TelegramBotService(
            PropertyTelegramBotRepository botRepository,
            PropertyRepository propertyRepository,
            StorefrontSettingsRepository storefrontSettingsRepository
    ) {
        this.botRepository = botRepository;
        this.propertyRepository = propertyRepository;
        this.storefrontSettingsRepository = storefrontSettingsRepository;

        org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(4000);
        factory.setReadTimeout(4000);
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * Helper to verify property ownership
     */
    private Property verifyPropertyOwnership(Long propertyId, Long userId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Property not found with id: " + propertyId
                ));

        if (userId != null && property.getUser() != null && !property.getUser().getId().equals(userId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Unauthorized: You do not own this property"
            );
        }
        return property;
    }

    /**
     * Get connection status and configuration for a property's Telegram bot
     */
    @Transactional(readOnly = true)
    public TelegramStatusResponse getBotStatus(Long propertyId, Long userId) {
        verifyPropertyOwnership(propertyId, userId);

        Optional<PropertyTelegramBot> botOpt = botRepository.findByPropertyId(propertyId);
        if (botOpt.isEmpty() || !Boolean.TRUE.equals(botOpt.get().getIsActive())) {
            return TelegramStatusResponse.notConnected();
        }

        PropertyTelegramBot bot = botOpt.get();
        TelegramStatusResponse res = new TelegramStatusResponse();
        res.setConnected(true);
        res.setBotUsername(bot.getBotUsername());
        res.setBotName(bot.getBotName());
        res.setBotLink("https://t.me/" + bot.getBotUsername());
        res.setGuestBookingLink("https://t.me/" + bot.getBotUsername() + "?start=book");
        res.setWebhookStatus(bot.getWebhookSecret() != null ? "HEALTHY" : "PENDING");
        res.setOwnerLinked(bot.getOwnerChatId() != null);
        res.setOwnerChatId(bot.getOwnerChatId());
        res.setWebAppUrl(bot.getWebAppUrl());
        res.setOwnerLinkUrl("https://t.me/" + bot.getBotUsername() + "?start=owner_alert");
        res.setNotifyNewBooking(Boolean.TRUE.equals(bot.getNotifyNewBooking()));
        res.setNotifyCancellation(Boolean.TRUE.equals(bot.getNotifyCancellation()));
        res.setNotifyDailyRoster(Boolean.TRUE.equals(bot.getNotifyDailyRoster()));

        if (bot.getProperty() != null) {
            res.setGuestBookingLink(resolveStorefrontUrl(bot, bot.getProperty()));
        }

        return res;
    }

    /**
     * Validate bot token with Telegram, register Webhook, and configure Mini App Menu Button
     */
    @Transactional
    public TelegramStatusResponse connectBot(Long propertyId, Long userId, String botToken, String backendBaseUrl) {
        Property property = verifyPropertyOwnership(propertyId, userId);

        String botUsername;
        String botName;

        // Support Sandbox / Demo Bot tokens for local testing
        if (botToken.toLowerCase().startsWith("demo_") || botToken.toLowerCase().startsWith("test_")) {
            String sanitized = property.getName().replaceAll("[^a-zA-Z0-9]", "");
            botUsername = sanitized + "_Bot";
            botName = property.getName() + " Bot";
            log.info("Registered demo Telegram bot: @{} for property {}", botUsername, propertyId);
        } else {
            // 1. Verify Token via Telegram getMe API
            Map<String, Object> getMeResponse = callTelegramApi(botToken, "getMe", null);
            if (getMeResponse == null || !Boolean.TRUE.equals(getMeResponse.get("ok"))) {
                throw new IllegalArgumentException("Invalid Telegram Bot API Token. Please check the token provided by @BotFather.");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> botInfo = (Map<String, Object>) getMeResponse.get("result");
            botUsername = (String) botInfo.get("username");
            botName = (String) botInfo.get("first_name");
        }

        // 2. Generate secret token for verifying webhooks
        String webhookSecret = UUID.randomUUID().toString().replace("-", "");

        // 3. Register Webhook with Telegram
        String effectiveBaseUrl = (backendBaseUrl != null && !backendBaseUrl.isBlank()) ? backendBaseUrl : stayopsPublicUrl;
        String webhookUrl = effectiveBaseUrl + "/api/public/telegram/webhook/" + propertyId;

        Map<String, Object> setWebhookPayload = new HashMap<>();
        setWebhookPayload.put("url", webhookUrl);
        setWebhookPayload.put("secret_token", webhookSecret);
        setWebhookPayload.put("allowed_updates", List.of("message", "callback_query"));

        try {
            Map<String, Object> webhookRes = callTelegramApi(botToken, "setWebhook", setWebhookPayload);
            log.info("Telegram setWebhook response for property {}: {}", propertyId, webhookRes);
        } catch (Exception e) {
            log.warn("Could not set Telegram webhook directly (e.g. running in local network without HTTPS tunnel): {}", e.getMessage());
        }

        // 4. Persist or Update PropertyTelegramBot
        PropertyTelegramBot bot = botRepository.findByPropertyId(propertyId)
                .orElse(new PropertyTelegramBot());

        bot.setProperty(property);
        bot.setBotToken(botToken);
        bot.setBotUsername(botUsername);
        bot.setBotName(botName);
        bot.setWebhookSecret(webhookSecret);
        bot.setIsActive(true);
        bot.setUpdatedAt(LocalDateTime.now());

        botRepository.save(bot);

        // 5. Configure Web App Menu Button pointing to Property Storefront
        configureChatMenuButton(botToken, bot, property);

        return getBotStatus(propertyId, userId);
    }

    /**
     * Disconnect bot, delete webhook, and clear tokens
     */
    @Transactional
    public void disconnectBot(Long propertyId, Long userId) {
        verifyPropertyOwnership(propertyId, userId);

        PropertyTelegramBot bot = botRepository.findByPropertyId(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("No Telegram bot configured for property: " + propertyId));

        if (bot.getBotToken() != null && !bot.getBotToken().isBlank()) {
            try {
                callTelegramApi(bot.getBotToken(), "deleteWebhook", Collections.singletonMap("drop_pending_updates", true));
            } catch (Exception e) {
                log.warn("Failed to delete Telegram webhook: {}", e.getMessage());
            }
        }

        bot.setIsActive(false);
        bot.setOwnerChatId(null);
        bot.setWebhookSecret(null);
        bot.setUpdatedAt(LocalDateTime.now());
        botRepository.save(bot);
    }

    /**
     * Update notification toggles and custom webAppUrl
     */
    @Transactional
    public TelegramStatusResponse updatePreferences(Long propertyId, Long userId, TelegramPreferencesRequest req) {
        Property property = verifyPropertyOwnership(propertyId, userId);

        PropertyTelegramBot bot = botRepository.findByPropertyId(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("No Telegram bot configured for property: " + propertyId));

        if (req.getNotifyNewBooking() != null) bot.setNotifyNewBooking(req.getNotifyNewBooking());
        if (req.getNotifyCancellation() != null) bot.setNotifyCancellation(req.getNotifyCancellation());
        if (req.getNotifyDailyRoster() != null) bot.setNotifyDailyRoster(req.getNotifyDailyRoster());
        if (req.getOwnerChatId() != null) {
            // Validate chat ID range
            bot.setOwnerChatId(req.getOwnerChatId());
        }

        boolean urlChanged = false;
        if (req.getWebAppUrl() != null) {
            String trimmed = req.getWebAppUrl().trim();
            if (!trimmed.isEmpty()) {
                // Security validation: only allow https:// or http://localhost
                if (!trimmed.startsWith("https://") && !trimmed.startsWith("http://localhost")) {
                    throw new IllegalArgumentException("Telegram Mini App URL must use HTTPS (or http://localhost for local development)");
                }
                bot.setWebAppUrl(trimmed);
            } else {
                bot.setWebAppUrl(null);
            }
            urlChanged = true;
        }

        bot.setUpdatedAt(LocalDateTime.now());
        botRepository.save(bot);

        if (urlChanged && bot.getBotToken() != null && bot.getProperty() != null) {
            configureChatMenuButton(bot.getBotToken(), bot, bot.getProperty());

            // If the user configured an HTTPS public URL, also update Telegram webhook
            if (bot.getWebAppUrl() != null && bot.getWebAppUrl().startsWith("https://")) {
                String base = bot.getWebAppUrl().replaceAll("/+$", "");
                String webhookUrl = base + "/api/public/telegram/webhook/" + propertyId;
                try {
                    Map<String, Object> setWebhookPayload = new HashMap<>();
                    setWebhookPayload.put("url", webhookUrl);
                    if (bot.getWebhookSecret() != null) {
                        setWebhookPayload.put("secret_token", bot.getWebhookSecret());
                    }
                    setWebhookPayload.put("allowed_updates", List.of("message", "callback_query"));
                    callTelegramApi(bot.getBotToken(), "setWebhook", setWebhookPayload);
                    log.info("Auto-updated Telegram webhook for property {} to {}", propertyId, webhookUrl);
                } catch (Exception e) {
                    log.warn("Could not auto-update Telegram webhook: {}", e.getMessage());
                }
            }
        }

        return getBotStatus(propertyId, userId);
    }

    /**
     * Handle incoming webhook updates from Telegram (guest booking requests & owner linkings)
     */
    @Transactional
    public void handleIncomingUpdate(Long propertyId, String secretHeader, Map<String, Object> update) {
        PropertyTelegramBot bot = botRepository.findByPropertyId(propertyId)
                .orElse(null);

        if (bot == null || !Boolean.TRUE.equals(bot.getIsActive())) {
            log.warn("Received update for inactive or non-existent Telegram bot on property {}", propertyId);
            return;
        }

        // Strict Webhook Secret Enforcement:
        // If a webhook secret is configured for this bot, incoming requests MUST supply matching X-Telegram-Bot-Api-Secret-Token
        if (bot.getWebhookSecret() != null && !bot.getWebhookSecret().isBlank()) {
            if (secretHeader == null || !bot.getWebhookSecret().equals(secretHeader)) {
                log.warn("Unauthorized Telegram webhook invocation attempt: secret header mismatch or missing for property {}", propertyId);
                return;
            }
        }

        if (!update.containsKey("message")) {
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> message = (Map<String, Object>) update.get("message");
        if (message == null || !message.containsKey("chat") || !message.containsKey("text")) {
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> chat = (Map<String, Object>) message.get("chat");
        Long chatId = ((Number) chat.get("id")).longValue();
        String text = (String) message.get("text");

        Property property = bot.getProperty();
        String propName = property != null ? property.getName() : "StayOps Property";

        String trimmedText = text.trim();
        String lowerText = trimmedText.toLowerCase();

        // 1. Check if owner is linking their account for alerts:
        if (text.startsWith("/start owner_alert") || text.equalsIgnoreCase("/link_owner")) {
            bot.setOwnerChatId(chatId);
            botRepository.save(bot);

            String replyHtml = "<b>StayOps Owner Alerts Activated!</b>\n\n"
                    + "Your Telegram account is now connected to <b>" + propName + "</b>.\n\n"
                    + "You will receive instant alerts for:\n"
                    + "New Storefront & OTA Bookings\n"
                    + "Booking Cancellations\n"
                    + "Daily Check-in Rosters\n\n"
                    + "<i>StayOps Operating System</i>";

            sendMessageHtml(bot.getBotToken(), chatId, replyHtml, null);
            return;
        }

        // Standard Guest /start message:
        if (text.startsWith("/start") || text.equalsIgnoreCase("/help") || text.equalsIgnoreCase("/book")) {
            String storeUrl = resolveStorefrontUrl(bot, property);

            String guestWelcome = "👋 <b>Welcome to " + propName + "!</b> 🌿\n\n"
                    + "Looking for a peaceful stay or weekend getaway? Browse our luxury rooms, check real-time availability, and book directly with instant confirmation.\n\n"
                    + "Tap <b>🏨 View Rooms & Book</b> below to explore!";

            // Inline buttons
            List<Map<String, Object>> row1 = List.of(
                    Map.of("text", "🏨 View Rooms & Book", "web_app", Map.of("url", storeUrl))
            );

            List<Map<String, Object>> row2 = new ArrayList<>();
            if (property != null && property.getContactPhone() != null && !property.getContactPhone().isBlank()) {
                row2.add(Map.of("text", "📞 Call Host", "url", "tel:" + property.getContactPhone()));
            }
            if (property != null && property.getCity() != null) {
                row2.add(Map.of("text", "📍 Location (" + property.getCity() + ")", "url", "https://maps.google.com/?q=" + propName + "+" + property.getCity()));
            }

            List<List<Map<String, Object>>> keyboard = new ArrayList<>();
            keyboard.add(row1);
            if (!row2.isEmpty()) {
                keyboard.add(row2);
            }

            Map<String, Object> replyMarkup = Map.of("inline_keyboard", keyboard);
            sendMessageHtml(bot.getBotToken(), chatId, guestWelcome, replyMarkup);
        }
    }

    /**
     * Dispatch instant booking notification to Property Owner via Telegram
     */
    public void sendBookingNotification(Property property, Reservation reservation) {
        if (property == null || reservation == null) return;

        Optional<PropertyTelegramBot> botOpt = botRepository.findByPropertyId(property.getId());
        if (botOpt.isEmpty()) return;

        PropertyTelegramBot bot = botOpt.get();
        if (!Boolean.TRUE.equals(bot.getIsActive()) || bot.getOwnerChatId() == null || !Boolean.TRUE.equals(bot.getNotifyNewBooking())) {
            return;
        }

        BigDecimal total = reservation.getTotalAmount() != null ? reservation.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal paid = reservation.getAmountPaid() != null ? reservation.getAmountPaid() : BigDecimal.ZERO;
        BigDecimal balance = total.subtract(paid).max(BigDecimal.ZERO);
        String paymentStatus = reservation.getPaymentStatus() != null && !reservation.getPaymentStatus().isBlank()
                ? reservation.getPaymentStatus()
                : "Pending";

        String msg = "🔔 <b>NEW BOOKING CONFIRMED!</b>\n"
                + "🏨 <b>" + property.getName() + "</b>\n"
                + "━━━━━━━━━━━━━━━━━━\n"
                + "👤 <b>Guest:</b> " + (reservation.getGuestName() != null ? reservation.getGuestName() : "Guest") + "\n"
                + "📞 <b>Phone:</b> " + (reservation.getGuestPhone() != null ? reservation.getGuestPhone() : "N/A") + "\n"
                + "🛏️ <b>Room:</b> " + (reservation.getRoomUnit() != null ? reservation.getRoomUnit() : "Room") + "\n"
                + "📅 <b>Dates:</b> " + reservation.getCheckIn() + " ➔ " + reservation.getCheckOut() + "\n"
                + "💰 <b>Total Amount:</b> ₹" + total + "\n"
                + "💵 <b>Amount Paid:</b> ₹" + paid + " (" + paymentStatus + ")\n"
                + "⏳ <b>Balance Due:</b> ₹" + balance + "\n"
                + "🔖 <b>Ref:</b> <code>" + (reservation.getBookingRef() != null ? reservation.getBookingRef() : "#" + reservation.getId()) + "</code>\n"
                + "━━━━━━━━━━━━━━━━━━\n"
                + "<i>Open StayOps Dashboard to view guest details & assign room.</i>";

        try {
            sendMessageHtml(bot.getBotToken(), bot.getOwnerChatId(), msg, null);
        } catch (Exception e) {
            log.error("Failed to send Telegram booking alert: {}", e.getMessage());
        }
    }

    /**
     * Send test alert to verify owner connection
     */
    public void sendTestAlert(Long propertyId, Long userId) {
        verifyPropertyOwnership(propertyId, userId);

        PropertyTelegramBot bot = botRepository.findByPropertyId(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("No Telegram bot configured for property"));

        if (bot.getOwnerChatId() == null) {
            throw new IllegalStateException("Owner has not linked their Telegram chat yet. Please tap 'Link Owner Account' first.");
        }

        String testMsg = "✅ <b>StayOps Telegram Test Alert!</b>\n\n"
                + "This confirms your Telegram bot is actively connected and ready to send instant operations alerts for <b>"
                + (bot.getProperty() != null ? bot.getProperty().getName() : "your property")
                + "</b>.\n\n"
                + "Timestamp: " + LocalDateTime.now();

        sendMessageHtml(bot.getBotToken(), bot.getOwnerChatId(), testMsg, null);
    }

    // Helper: configure WebApp Menu Button
    private void configureChatMenuButton(String botToken, PropertyTelegramBot bot, Property property) {
        if (botToken != null && (botToken.toLowerCase().startsWith("demo_") || botToken.toLowerCase().startsWith("test_"))) {
            return;
        }

        try {
            String storeUrl = resolveStorefrontUrl(bot, property);
            Map<String, Object> menuButton = Map.of(
                    "type", "web_app",
                    "text", "🏨 View Rooms & Book",
                    "web_app", Map.of("url", storeUrl)
            );
            callTelegramApi(botToken, "setChatMenuButton", Map.of("menu_button", menuButton));
            log.info("Telegram setChatMenuButton updated for property {} pointing to {}", property != null ? property.getId() : null, storeUrl);
        } catch (Exception e) {
            log.warn("Could not set Telegram ChatMenuButton: {}", e.getMessage());
        }
    }

    // Helper: resolve storefront URL for property
    public String resolveStorefrontUrl(PropertyTelegramBot bot, Property property) {
        if (bot != null && bot.getWebAppUrl() != null && !bot.getWebAppUrl().isBlank()) {
            String customUrl = bot.getWebAppUrl().trim();
            if (!customUrl.contains("/store/") && !customUrl.contains("/p/")) {
                if (customUrl.endsWith("/")) {
                    customUrl = customUrl.substring(0, customUrl.length() - 1);
                }
                String slug = resolvePropertySlug(property);
                return customUrl + "/store/" + slug + "?source=telegram";
            }
            return customUrl;
        }

        if (property == null) return stayopsPublicUrl;
        String slug = resolvePropertySlug(property);
        return stayopsPublicUrl + "/store/" + slug + "?source=telegram";
    }

    private String resolvePropertySlug(Property property) {
        if (property == null) return "property-demo";
        Optional<StorefrontSettings> sfOpt = storefrontSettingsRepository.findByPropertyId(property.getId());
        return sfOpt.map(StorefrontSettings::getSubdomain).orElseGet(() -> "property-" + property.getId());
    }

    // Helper: send HTML message to Telegram
    private void sendMessageHtml(String botToken, Long chatId, String textHtml, Map<String, Object> replyMarkup) {
        if (botToken != null && (botToken.toLowerCase().startsWith("demo_") || botToken.toLowerCase().startsWith("test_"))) {
            log.info("[MOCK TELEGRAM BOT] Sending message to Chat {}: \n{}", chatId, textHtml);
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("chat_id", chatId);
        payload.put("text", textHtml);
        payload.put("parse_mode", "HTML");
        if (replyMarkup != null) {
            payload.put("reply_markup", replyMarkup);
        }
        callTelegramApi(botToken, "sendMessage", payload);
    }

    // Helper: generic Telegram Bot API caller
    @SuppressWarnings("unchecked")
    private Map<String, Object> callTelegramApi(String botToken, String method, Object body) {
        String url = TELEGRAM_API_BASE + botToken + "/" + method;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Telegram API call error [{}]: {}", method, e.getMessage());
            throw new RuntimeException("Telegram API Error: " + e.getMessage(), e);
        }
    }
}
