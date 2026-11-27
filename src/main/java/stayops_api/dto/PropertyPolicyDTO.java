package stayops_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Property House Rules & Cancellation Policy DTO")
public class PropertyPolicyDTO {

    @Schema(description = "Is Non-Refundable / No Cancellation Policy Active", example = "false")
    private Boolean noCancellationPolicy;

    @Schema(description = "Tiered Cancellation Rules JSON String Array", example = "[{\"id\":\"rule-1\",\"daysBeforeCheckIn\":7,\"refundPercentage\":100},{\"id\":\"rule-2\",\"daysBeforeCheckIn\":3,\"refundPercentage\":50}]")
    private String cancellationRulesJson;

    @Schema(description = "Terms & Conditions Text", example = "Valid government photo ID required at check-in. No smoking inside rooms.")
    private String termsAndConditions;

    @Schema(description = "Privacy Policy Text", example = "Guest information is kept strictly confidential and used for stay management only.")
    private String privacyPolicy;

    @Schema(description = "Refund Processing Policy", example = "Refunds are processed to original payment method within 5 to 7 business days.")
    private String refundPolicy;

    @Schema(description = "Child & Extra Bed Age Rules JSON String Array", example = "[{\"id\":\"c-1\",\"maxAge\":5,\"chargeType\":\"Free\",\"rate\":0},{\"id\":\"c-2\",\"maxAge\":12,\"chargeType\":\"Child Charge\",\"rate\":500}]")
    private String childAgePolicyJson;

    public PropertyPolicyDTO() {}

    public PropertyPolicyDTO(
            Boolean noCancellationPolicy,
            String cancellationRulesJson,
            String termsAndConditions,
            String privacyPolicy,
            String refundPolicy,
            String childAgePolicyJson
    ) {
        this.noCancellationPolicy = noCancellationPolicy;
        this.cancellationRulesJson = cancellationRulesJson;
        this.termsAndConditions = termsAndConditions;
        this.privacyPolicy = privacyPolicy;
        this.refundPolicy = refundPolicy;
        this.childAgePolicyJson = childAgePolicyJson;
    }

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
}
