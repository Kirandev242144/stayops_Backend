package stayops_api.entity;

import jakarta.persistence.*;
import stayops_api.security.SecretEncryptionConverter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_gateway_credentials", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "gateway_name"})
})
public class PaymentGatewayCredentials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "gateway_name", nullable = false, length = 50)
    private String gatewayName = "RAZORPAY";

    @Column(name = "key_id")
    private String keyId;

    @Column(name = "key_secret", length = 512)
    @Convert(converter = SecretEncryptionConverter.class)
    private String keySecret;

    @Column(name = "webhook_secret", length = 512)
    @Convert(converter = SecretEncryptionConverter.class)
    private String webhookSecret;

    @Column(name = "is_test_mode")
    private Boolean isTestMode = true;

    @Column(name = "connection_status", length = 30)
    private String connectionStatus = "not-connected";

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public PaymentGatewayCredentials() {
    }

    public PaymentGatewayCredentials(User user, String gatewayName, String keyId, String keySecret, String webhookSecret) {
        this.user = user;
        this.gatewayName = gatewayName;
        this.keyId = keyId;
        this.keySecret = keySecret;
        this.webhookSecret = webhookSecret;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeySecret() {
        return keySecret;
    }

    public void setKeySecret(String keySecret) {
        this.keySecret = keySecret;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public Boolean getTestMode() {
        return isTestMode;
    }

    public void setTestMode(Boolean testMode) {
        isTestMode = testMode;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
