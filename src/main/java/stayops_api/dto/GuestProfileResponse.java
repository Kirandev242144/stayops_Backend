package stayops_api.dto;

import java.time.LocalDateTime;

public class GuestProfileResponse {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String idType;
    private String idNumber;
    private String idDocumentUrl;
    private String idDocuments;
    private String privateNotes;
    private LocalDateTime createdAt;

    public GuestProfileResponse() {
    }

    public GuestProfileResponse(
            Long id,
            String name,
            String phone,
            String email,
            String idNumber,
            String idDocumentUrl,
            String privateNotes,
            LocalDateTime createdAt
    ) {
        this(id, name, phone, email, null, idNumber, idDocumentUrl, null, privateNotes, createdAt);
    }

    public GuestProfileResponse(
            Long id,
            String name,
            String phone,
            String email,
            String idType,
            String idNumber,
            String idDocumentUrl,
            String idDocuments,
            String privateNotes,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.idType = idType;
        this.idNumber = idNumber;
        this.idDocumentUrl = idDocumentUrl;
        this.idDocuments = idDocuments;
        this.privateNotes = privateNotes;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getIdType() {
        return idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getIdDocumentUrl() {
        return idDocumentUrl;
    }

    public String getIdDocuments() {
        return idDocuments;
    }

    public String getPrivateNotes() {
        return privateNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
