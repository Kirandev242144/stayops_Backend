package stayops_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User Authentication Response Token Payload")
public class LoginResponse {

    @Schema(description = "JWT Authorization Token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYW5keUBzdGF5b3BzLnRlc3QiLCJ1c2VySWQiOjEwfQ.sample")
    private String token;

    @Schema(description = "User ID", example = "10")
    private Long userId;

    @Schema(description = "Owner Full Name", example = "Randy Stevens")
    private String name;

    @Schema(description = "Owner Registered Email", example = "randy@stayops.test")
    private String email;

    public LoginResponse() {
    }

    // Token
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // User ID
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}