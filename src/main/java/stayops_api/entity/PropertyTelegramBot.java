package stayops_api.entity;

import jakarta.persistence.*;
import stayops_api.security.SecretEncryptionConverter;

import java.time.LocalDateTime;

@Entity
@Table(name = "property_telegram_bots")
public class PropertyTelegramBot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false, unique = true)
    private Property property;

    @Column(name = "bot_token", length = 512)
    @Convert(converter = SecretEncryptionConverter.class)
    private String botToken;

    @Column(name = "bot_username", length = 100)
    private String botUsername;

    @Column(name = "bot_name", length = 150)
    private String botName;

    @Column(name = "webhook_secret", length = 100)
    private String webhookSecret;

    @Column(name = "owner_chat_id")
    private Long ownerChatId;

    @Column(name = "is_active")
    private Boolean isActive = false;

    @Column(name = "notify_new_booking")
    private Boolean notifyNewBooking = true;

    @Column(name = "notify_cancellation")
    private Boolean notifyCancellation = true;

    @Column(name = "notify_daily_roster")
    private Boolean notifyDailyRoster = true;

    @Column(name = "web_app_url", length = 512)
    private String webAppUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public PropertyTelegramBot() {
    }

    public PropertyTelegramBot(Property property, String botToken, String botUsername, String botName, String webhookSecret) {
        this.property = property;
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.botName = botName;
        this.webhookSecret = webhookSecret;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String getBotUsername() {
        return botUsername;
    }

    public void setBotUsername(String botUsername) {
        this.botUsername = botUsername;
    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public Long getOwnerChatId() {
        return ownerChatId;
    }

    public void setOwnerChatId(Long ownerChatId) {
        this.ownerChatId = ownerChatId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public Boolean getNotifyNewBooking() {
        return notifyNewBooking;
    }

    public void setNotifyNewBooking(Boolean notifyNewBooking) {
        this.notifyNewBooking = notifyNewBooking;
    }

    public Boolean getNotifyCancellation() {
        return notifyCancellation;
    }

    public void setNotifyCancellation(Boolean notifyCancellation) {
        this.notifyCancellation = notifyCancellation;
    }

    public Boolean getNotifyDailyRoster() {
        return notifyDailyRoster;
    }

    public void setNotifyDailyRoster(Boolean notifyDailyRoster) {
        this.notifyDailyRoster = notifyDailyRoster;
    }

    public String getWebAppUrl() {
        return webAppUrl;
    }

    public void setWebAppUrl(String webAppUrl) {
        this.webAppUrl = webAppUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
