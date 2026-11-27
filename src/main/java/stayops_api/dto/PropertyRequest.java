package stayops_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Property Creation Payload")
public class PropertyRequest {

    @Schema(description = "Property Title / Display Name", example = "Sri Nature Homestay & Retreat")
    private String name;

    @Schema(description = "Property Category (Villa, Hotel, Homestay, Resort)", example = "Homestay")
    private String type;

    @Schema(description = "Property Description", example = "A serene coffee estate sanctuary featuring private cottages and mountain views.")
    private String description;

    @Schema(description = "Street Address", example = "12 Estate Road, Mallandur")
    private String address;

    @Schema(description = "City", example = "Chikmagalur")
    private String city;

    @Schema(description = "State", example = "Karnataka")
    private String state;

    @Schema(description = "Postal Pin Code", example = "577101")
    private String postalCode;

    @Schema(description = "Tourism / Municipal Permit License Number", example = "KA-KAR-2024-9982")
    private String permitId;

    @Schema(description = "Contact Email", example = "info@srinature.com")
    private String contactEmail;

    @Schema(description = "Contact Phone Number", example = "+91 9876543210")
    private String contactPhone;

    @Schema(description = "Base Starting Nightly Rate (INR)", example = "3500")
    private Integer baseRate;

    @Schema(description = "Is Entire Property Rented as Villa", example = "false")
    private Boolean isEntireVilla;

    @Schema(description = "Total Inventory Room Count", example = "6")
    private Integer rooms;

    @Schema(description = "Room Categories JSON String Array", example = "[{\"id\":\"rt-1\",\"name\":\"Garden View Cottage\",\"beds\":2,\"maxGuests\":4,\"ratePerNight\":3500,\"amenities\":[\"Free WiFi\",\"Air Conditioning\"],\"inclusions\":[\"Breakfast\"]}]")
    private String roomTypesJson;

    @Schema(description = "Operations Configuration JSON String", example = "{\"checkInTime\":\"12:00 PM\",\"checkOutTime\":\"11:00 AM\",\"themeConfig\":{\"subdomain\":\"srinature\"}}")
    private String operationsJson;

    @Schema(description = "Property Amenities JSON String Array", example = "[\"Free WiFi\",\"In-house Kitchen\",\"Free Parking\",\"Private Garden\",\"24/7 Security\",\"Power Backup\"]")
    private String propertyAmenitiesJson;

    @Schema(description = "Latitude coordinate", example = "12.4244")
    private java.math.BigDecimal lat;

    @Schema(description = "Longitude coordinate", example = "75.7382")
    private java.math.BigDecimal lng;

    public PropertyRequest() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(Integer baseRate) {
        this.baseRate = baseRate;
    }

    public Boolean getIsEntireVilla() {
        return isEntireVilla;
    }

    public void setIsEntireVilla(Boolean entireVilla) {
        isEntireVilla = entireVilla;
    }

    public Integer getRooms() {
        return rooms;
    }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }

    public String getRoomTypesJson() {
        return roomTypesJson;
    }

    public void setRoomTypesJson(String roomTypesJson) {
        this.roomTypesJson = roomTypesJson;
    }

    public String getOperationsJson() {
        return operationsJson;
    }

    public void setOperationsJson(String operationsJson) {
        this.operationsJson = operationsJson;
    }

    public String getPropertyAmenitiesJson() {
        return propertyAmenitiesJson;
    }

    public void setPropertyAmenitiesJson(String propertyAmenitiesJson) {
        this.propertyAmenitiesJson = propertyAmenitiesJson;
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
