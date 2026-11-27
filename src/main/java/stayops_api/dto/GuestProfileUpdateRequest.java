package stayops_api.dto;

public class GuestProfileUpdateRequest {

    private String name;
    private String email;
    private String idType;
    private String idNumber;
    private String idDocumentUrl;
    private String idDocuments;
    private String privateNotes;

    public GuestProfileUpdateRequest() {
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

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIdDocumentUrl() {
        return idDocumentUrl;
    }

    public void setIdDocumentUrl(String idDocumentUrl) {
        this.idDocumentUrl = idDocumentUrl;
    }

    public String getIdDocuments() {
        return idDocuments;
    }

    public void setIdDocuments(String idDocuments) {
        this.idDocuments = idDocuments;
    }

    public String getPrivateNotes() {
        return privateNotes;
    }

    public void setPrivateNotes(String privateNotes) {
        this.privateNotes = privateNotes;
    }
}
