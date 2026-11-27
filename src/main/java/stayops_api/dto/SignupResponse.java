package stayops_api.dto;

public class SignupResponse {

    private Long id;
    private String name;
    private String email;
    private String token;
    private String plan;
    private Integer trialDaysLeft;
    private Long propertyId;
    private String propertyName;

    public SignupResponse() {
    }

    public SignupResponse(
            Long id,
            String name,
            String email
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public Integer getTrialDaysLeft() {
        return trialDaysLeft;
    }

    public void setTrialDaysLeft(Integer trialDaysLeft) {
        this.trialDaysLeft = trialDaysLeft;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
}