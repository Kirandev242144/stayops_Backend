package stayops_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Storefront Studio Customizer Settings DTO")
public class StorefrontSettingsDTO {

    private Long id;
    private Long propertyId;

    @Schema(description = "Unique Subdomain Slug", example = "srinature")
    private String subdomain;

    @Schema(description = "Style Preset Name", example = "slate")
    private String preset;

    @Schema(description = "Primary Brand Color Hex", example = "#111827")
    private String primaryColor;

    @Schema(description = "Secondary Accent Color Hex", example = "#C5A059")
    private String secondaryColor;

    @Schema(description = "Hero Tagline / Welcome Headline Text", example = "Several Cottages — One Host, Many Places to Stay")
    private String tagline;

    public StorefrontSettingsDTO() {}

    public StorefrontSettingsDTO(Long id, Long propertyId, String subdomain, String preset, String primaryColor, String secondaryColor, String tagline) {
        this.id = id;
        this.propertyId = propertyId;
        this.subdomain = subdomain;
        this.preset = preset;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.tagline = tagline;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

    public String getSubdomain() { return subdomain; }
    public void setSubdomain(String subdomain) { this.subdomain = subdomain; }

    public String getPreset() { return preset; }
    public void setPreset(String preset) { this.preset = preset; }

    public String getPrimaryColor() { return primaryColor; }
    public void setPrimaryColor(String primaryColor) { this.primaryColor = primaryColor; }

    public String getSecondaryColor() { return secondaryColor; }
    public void setSecondaryColor(String secondaryColor) { this.secondaryColor = secondaryColor; }

    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }
}
