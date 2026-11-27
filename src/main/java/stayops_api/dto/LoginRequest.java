package stayops_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User Authentication Login Payload")
public class LoginRequest {

    @Schema(description = "Property Owner Email", example = "randy@stayops.test")
    private String email;

    @Schema(description = "Account Password", example = "password123")
    private String password;

    @Schema(description = "Property Subdomain Slug (optional, for tenant authorization)", example = "dadad")
    private String subdomain;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }
}