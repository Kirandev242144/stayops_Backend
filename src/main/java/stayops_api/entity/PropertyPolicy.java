package stayops_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "property_policies")
public class PropertyPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false, unique = true)
    private Property property;

    private Boolean noCancellationPolicy = false;

    @Column(columnDefinition = "TEXT")
    private String cancellationRulesJson;

    @Column(columnDefinition = "TEXT")
    private String termsAndConditions;

    @Column(columnDefinition = "TEXT")
    private String privacyPolicy;

    @Column(columnDefinition = "TEXT")
    private String refundPolicy;

    @Column(columnDefinition = "TEXT")
    private String childAgePolicyJson;

    private LocalDateTime updatedAt;

    public PropertyPolicy() {}

    public PropertyPolicy(Property property) {
        this.property = property;
        this.noCancellationPolicy = false;
        this.cancellationRulesJson = "[]";
        this.termsAndConditions = "Defines the rules, responsibilities, and agreements for using our stay services and products. Standard check-in is 12:00 PM and check-out is 11:00 AM. Valid government photo ID is required for all adult guests at check-in.";
        this.privacyPolicy = "Outlines how we collect, use, and protect your personal information. Your contact details are stored securely and used solely for stay reservations and guest communication.";
        this.refundPolicy = "Explains the conditions and process for requesting and receiving refunds. Approved refunds are processed back to the original payment method within 5-7 business days.";
        this.childAgePolicyJson = "{\"freeChildMaxAge\":5,\"childAgeMax\":12,\"childExtraCharge\":500}";
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    public void onSave() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }

    public Boolean getNoCancellationPolicy() { return noCancellationPolicy; }
    public void setNoCancellationPolicy(Boolean noCancellationPolicy) { this.noCancellationPolicy = noCancellationPolicy; }

    public String getCancellationRulesJson() { return cancellationRulesJson; }
    public void setCancellationRulesJson(String cancellationRulesJson) { this.cancellationRulesJson = cancellationRulesJson; }

    public String getTermsAndConditions() { return termsAndConditions; }
    public void setTermsAndConditions(String termsAndConditions) { this.termsAndConditions = termsAndConditions; }

    public String getPrivacyPolicy() { return privacyPolicy; }
    public void setPrivacyPolicy(String privacyPolicy) { this.privacyPolicy = privacyPolicy; }

    public String getRefundPolicy() { return refundPolicy; }
    public void setRefundPolicy(String refundPolicy) { this.refundPolicy = refundPolicy; }

    public String getChildAgePolicyJson() { return childAgePolicyJson; }
    public void setChildAgePolicyJson(String childAgePolicyJson) { this.childAgePolicyJson = childAgePolicyJson; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
