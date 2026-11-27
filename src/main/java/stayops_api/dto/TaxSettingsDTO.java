package stayops_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "GST & Service Charge Configuration DTO")
public class TaxSettingsDTO {

    @Schema(description = "Is GST Registered Entity", example = "true")
    private Boolean isGstRegistered = false;

    @Schema(description = "15-digit GSTIN License Number", example = "29ABCDE1234F1Z5")
    private String gstin;

    @Schema(description = "Enable Taxes on Invoices", example = "true")
    private Boolean taxesEnabled = true;

    @Schema(description = "Tax Mode (SLAB_INDIA_GST or FIXED_PERCENTAGE)", example = "SLAB_INDIA_GST")
    private String taxMode = "SLAB_INDIA_GST";

    @Schema(description = "Fixed Flat Tax Rate (if taxMode is FIXED_PERCENTAGE)", example = "12.0")
    private Double fixedTaxRate = 12.0;

    @Schema(description = "Is Tariff Inclusive of GST", example = "false")
    private Boolean taxInclusive = false;

    @Schema(description = "Enable Service Charge", example = "true")
    private Boolean serviceChargeEnabled = false;

    @Schema(description = "Service Charge Percentage", example = "5.0")
    private Double serviceChargePercent = 5.0;

    @Schema(description = "Specified Premises Classification", example = "DECLARED")
    private String specifiedPremises = "NOT_DECLARED";

    public TaxSettingsDTO() {
    }

    public TaxSettingsDTO(Boolean isGstRegistered, String gstin, Boolean taxesEnabled, String taxMode,
                          Double fixedTaxRate, Boolean taxInclusive, Boolean serviceChargeEnabled,
                          Double serviceChargePercent, String specifiedPremises) {
        this.isGstRegistered = isGstRegistered;
        this.gstin = gstin;
        this.taxesEnabled = taxesEnabled;
        this.taxMode = taxMode;
        this.fixedTaxRate = fixedTaxRate;
        this.taxInclusive = taxInclusive;
        this.serviceChargeEnabled = serviceChargeEnabled;
        this.serviceChargePercent = serviceChargePercent;
        this.specifiedPremises = specifiedPremises;
    }

    public Boolean getIsGstRegistered() {
        return isGstRegistered;
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
        return taxesEnabled;
    }

    public void setTaxesEnabled(Boolean taxesEnabled) {
        this.taxesEnabled = taxesEnabled;
    }

    public String getTaxMode() {
        return taxMode;
    }

    public void setTaxMode(String taxMode) {
        this.taxMode = taxMode;
    }

    public Double getFixedTaxRate() {
        return fixedTaxRate;
    }

    public void setFixedTaxRate(Double fixedTaxRate) {
        this.fixedTaxRate = fixedTaxRate;
    }

    public Boolean getTaxInclusive() {
        return taxInclusive;
    }

    public void setTaxInclusive(Boolean taxInclusive) {
        this.taxInclusive = taxInclusive;
    }

    public Boolean getServiceChargeEnabled() {
        return serviceChargeEnabled;
    }

    public void setServiceChargeEnabled(Boolean serviceChargeEnabled) {
        this.serviceChargeEnabled = serviceChargeEnabled;
    }

    public Double getServiceChargePercent() {
        return serviceChargePercent;
    }

    public void setServiceChargePercent(Double serviceChargePercent) {
        this.serviceChargePercent = serviceChargePercent;
    }

    public String getSpecifiedPremises() {
        return specifiedPremises;
    }

    public void setSpecifiedPremises(String specifiedPremises) {
        this.specifiedPremises = specifiedPremises;
    }
}
