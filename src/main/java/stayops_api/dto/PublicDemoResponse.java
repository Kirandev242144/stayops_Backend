package stayops_api.dto;

public class PublicDemoResponse {

    private Long id;
    private boolean success;
    private String message;

    public PublicDemoResponse() {
    }

    public PublicDemoResponse(Long id, boolean success, String message) {
        this.id = id;
        this.success = success;
        this.message = message;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
