package stayops_api.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 100)
    private String type;

    @Column(columnDefinition = "TEXT")
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "permit_id", length = 100)
    private String permitId;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    private BigDecimal lat;

    private BigDecimal lng;

    private Integer rooms;

    @Column(length = 50)
    private String status;

    private BigDecimal rating;

    private Integer reviews;

    @Column(length = 50)
    private String revenue;

    private Integer occupancy;

    @Column(columnDefinition = "JSON")
    private String channels;

    @Column(length = 255)
    private String storefront;

    @Column(columnDefinition = "TEXT")
    private String photo;

    @Column(name = "base_rate")
    private Integer baseRate;

    @Column(name = "is_entire_villa")
    private Boolean isEntireVilla;

    @Column(columnDefinition = "JSON", name = "room_types")
    private String roomTypes;

    @Column(columnDefinition = "JSON", name = "property_amenities")
    private String propertyAmenities;

    @Column(columnDefinition = "JSON")
    private String pricing;

    @Column(columnDefinition = "JSON")
    private String operations;

    @Column(length = 255)
    private String caretaker;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    public Property() {
    }


    public Long getId() {
        return id;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }


    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
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


    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }


    public Integer getReviews() {
        return reviews;
    }

    public void setReviews(Integer reviews) {
        this.reviews = reviews;
    }


    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }


    public Integer getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(Integer occupancy) {
        this.occupancy = occupancy;
    }


    public String getChannels() {
        return channels;
    }

    public void setChannels(String channels) {
        this.channels = channels;
    }


    public String getStorefront() {
        return storefront;
    }

    public void setStorefront(String storefront) {
        this.storefront = storefront;
    }


    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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


    public String getCaretaker() {
        return caretaker;
    }

    public void setCaretaker(String caretaker) {
        this.caretaker = caretaker;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
}