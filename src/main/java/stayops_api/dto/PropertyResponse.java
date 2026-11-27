package stayops_api.dto;

import java.util.List;

public class PropertyResponse {

    private Long id;
    private String name;
    private String type;
    private String location;
    private String description;
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
    private java.math.BigDecimal lat;
    private java.math.BigDecimal lng;

    public PropertyResponse() {
    }

    public PropertyResponse(
            Long id,
            String name,
            String type,
            String location,
            String description,
            String address,
            String city,
            String state,
            String postalCode,
            String permitId,
            String contactEmail,
            String contactPhone,
            Integer rooms,
            String status,
            Integer baseRate,
            Boolean isEntireVilla,
            List<String> propertyPhotos,
            String roomTypes,
            String propertyAmenities,
            String pricing,
            String operations,
            java.math.BigDecimal lat,
            java.math.BigDecimal lng
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.location = location;
        this.description = description;
        this.address = address;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.permitId = permitId;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.rooms = rooms;
        this.status = status;
        this.baseRate = baseRate;
        this.isEntireVilla = isEntireVilla;
        this.propertyPhotos = propertyPhotos;
        this.roomTypes = roomTypes;
        this.propertyAmenities = propertyAmenities;
        this.pricing = pricing;
        this.operations = operations;
        this.lat = lat;
        this.lng = lng;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPermitId() {
        return permitId;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public Integer getRooms() {
        return rooms;
    }

    public String getStatus() {
        return status;
    }

    public Integer getBaseRate() {
        return baseRate;
    }

    public Boolean getIsEntireVilla() {
        return isEntireVilla;
    }

    public List<String> getPropertyPhotos() {
        return propertyPhotos;
    }

    public String getRoomTypes() {
        return roomTypes;
    }

    public String getPropertyAmenities() {
        return propertyAmenities;
    }

    public String getPricing() {
        return pricing;
    }

    public String getOperations() {
        return operations;
    }

    public java.math.BigDecimal getLat() {
        return lat;
    }

    public void setLat(java.math.BigDecimal lat) {
        this.lat = lat;
    }

    public java.math.BigDecimal getLng() {
        return lng;
    }

    public void setLng(java.math.BigDecimal lng) {
        this.lng = lng;
    }
}