package stayops_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PublicDemoRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Business / Property name is required")
    private String businessName;

    @NotBlank(message = "Email address is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotNull(message = "Number of properties is required")
    @Min(value = 1, message = "Minimum 1 property is required")
    private Integer propertyCount;

    @NotBlank(message = "Property type is required")
    private String propertyType;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    private String preferredContactMethod = "WhatsApp";

    private String message;

    public PublicDemoRequest() {
    }

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Integer getPropertyCount() { return propertyCount; }
    public void setPropertyCount(Integer propertyCount) { this.propertyCount = propertyCount; }

    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPreferredContactMethod() { return preferredContactMethod; }
    public void setPreferredContactMethod(String preferredContactMethod) { this.preferredContactMethod = preferredContactMethod; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
