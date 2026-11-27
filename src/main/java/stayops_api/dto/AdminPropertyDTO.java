package stayops_api.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AdminPropertyDTO {

    private Long id;
    private String name;
    private String type;
    private String category;
    private String code;
    private String location;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String permitId;
    private String contactEmail;
    private String contactPhone;
    private Integer rooms;
    private String status;
    private Integer baseRate;
    private Boolean isEntireVilla;
    private List<String> propertyPhotos;
    private String roomTypes;
    private String propertyAmenities;
    private String pricing;
    private String operations;
    private String storefront;
    private String slug;
    private LocalDateTime createdAt;

    // Owner Identity
    private Long ownerId;
    private String ownerName;
    private String ownerEmail;
    private String ownerPhone;
    private String subscriptionPlan;
    private List<AdminAuditLogDTO> auditLog;

    public AdminPropertyDTO() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPermitId() {
        return permitId;
    }

    public void setPermitId(String permitId) {
        this.permitId = permitId;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Integer getRooms() {
        return rooms;
    }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(Integer baseRate) {
        this.baseRate = baseRate;
    }

    public Boolean getIsEntireVilla() {
        return isEntireVilla;
    }

    public void setIsEntireVilla(Boolean isEntireVilla) {
        this.isEntireVilla = isEntireVilla;
    }

    public List<String> getPropertyPhotos() {
        return propertyPhotos;
    }

    public void setPropertyPhotos(List<String> propertyPhotos) {
        this.propertyPhotos = propertyPhotos;
    }

    public String getRoomTypes() {
        return roomTypes;
    }

    public void setRoomTypes(String roomTypes) {
        this.roomTypes = roomTypes;
    }

    public String getPropertyAmenities() {
        return propertyAmenities;
    }

    public void setPropertyAmenities(String propertyAmenities) {
        this.propertyAmenities = propertyAmenities;
    }

    public String getPricing() {
        return pricing;
    }

    public void setPricing(String pricing) {
        this.pricing = pricing;
    }

    public String getOperations() {
        return operations;
    }

    public void setOperations(String operations) {
        this.operations = operations;
    }

    public String getStorefront() {
        return storefront;
    }

    public void setStorefront(String storefront) {
        this.storefront = storefront;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(String subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public List<AdminAuditLogDTO> getAuditLog() {
        return auditLog;
    }

    public void setAuditLog(List<AdminAuditLogDTO> auditLog) {
        this.auditLog = auditLog;
    }
}
