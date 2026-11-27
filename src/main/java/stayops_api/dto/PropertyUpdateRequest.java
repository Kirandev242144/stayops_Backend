package stayops_api.dto;

public class PropertyUpdateRequest {
    private String name;
    private String type;
    private String description;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String location;
    private String status;
    private Integer baseRate;
    
    // JSON strings
    private String roomTypes;
    private String propertyAmenities;
    private String pricing;
    private String operations;

    private int rooms;
    private java.math.BigDecimal lat;
    private java.math.BigDecimal lng;

    public PropertyUpdateRequest() {}

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
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
