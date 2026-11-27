package stayops_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tax_settings")
public class TaxSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false, unique = true)
    private Property property;

    @Column(name = "is_gst_registered")
    private Boolean isGstRegistered = false;

    @Column(name = "gstin", length = 20)
    private String gstin;

    @Column(name = "taxes_enabled")
    private Boolean taxesEnabled = true;

    @Column(name = "tax_mode", length = 30)
    private String taxMode = "SLAB_INDIA_GST"; // SLAB_INDIA_GST, FIXED_PERCENT, EXEMPT

    @Column(name = "fixed_tax_rate")
    private Double fixedTaxRate = 12.0;

    @Column(name = "tax_inclusive")
    private Boolean taxInclusive = false;

    @Column(name = "service_charge_enabled")
    private Boolean serviceChargeEnabled = false;

    @Column(name = "service_charge_percent")
    private Double serviceChargePercent = 10.0;

    @Column(name = "specified_premises", length = 30)
    private String specifiedPremises = "NOT_DECLARED"; // NOT_DECLARED, YES, NO

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public TaxSettings() {
    }

    public TaxSettings(Property property) {
        this.property = property;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Boolean getIsGstRegistered() {
        return isGstRegistered != null ? isGstRegistered : false;
    }

    public void setIsGstRegistered(Boolean isGstRegistered) {
        this.isGstRegistered = isGstRegistered;
    }

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public Boolean getTaxesEnabled() {
        return taxesEnabled != null ? taxesEnabled : true;
    }

    public void setTaxesEnabled(Boolean taxesEnabled) {
        this.taxesEnabled = taxesEnabled;
    }

    public String getTaxMode() {
        return taxMode != null ? taxMode : "SLAB_INDIA_GST";
    }

    public void setTaxMode(String taxMode) {
        this.taxMode = taxMode;
    }

    public Double getFixedTaxRate() {
        return fixedTaxRate != null ? fixedTaxRate : 12.0;
    }

    public void setFixedTaxRate(Double fixedTaxRate) {
        this.fixedTaxRate = fixedTaxRate;
    }

    public Boolean getTaxInclusive() {
        return taxInclusive != null ? taxInclusive : false;
    }

    public void setTaxInclusive(Boolean taxInclusive) {
        this.taxInclusive = taxInclusive;
    }

    public Boolean getServiceChargeEnabled() {
        return serviceChargeEnabled != null ? serviceChargeEnabled : false;
    }

    public void setServiceChargeEnabled(Boolean serviceChargeEnabled) {
        this.serviceChargeEnabled = serviceChargeEnabled;
    }

    public Double getServiceChargePercent() {
        return serviceChargePercent != null ? serviceChargePercent : 10.0;
    }

    public void setServiceChargePercent(Double serviceChargePercent) {
        this.serviceChargePercent = serviceChargePercent;
    }

    public String getSpecifiedPremises() {
        return specifiedPremises != null ? specifiedPremises : "NOT_DECLARED";
    }

    public void setSpecifiedPremises(String specifiedPremises) {
        this.specifiedPremises = specifiedPremises;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
