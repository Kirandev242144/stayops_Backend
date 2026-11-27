package stayops_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stayops_api.dto.PropertyRequest;
import stayops_api.dto.PropertyResponse;
import stayops_api.entity.Property;
import stayops_api.entity.User;
import stayops_api.entity.PropertyPhoto;
import stayops_api.entity.Reservation;
import stayops_api.entity.MealOrder;
import stayops_api.repository.MealOrderRepository;
import stayops_api.repository.PropertyPolicyRepository;
import stayops_api.repository.PropertyPhotoRepository;
import stayops_api.repository.PropertyRepository;
import stayops_api.repository.UserRepository;
import stayops_api.repository.ReservationRepository;
import stayops_api.repository.RoomBlockedDateRepository;
import stayops_api.repository.TaxSettingsRepository;
import stayops_api.entity.StorefrontSettings;
import stayops_api.repository.StorefrontSettingsRepository;

import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final RoomBlockedDateRepository blockedDateRepository;
    private final PropertyPhotoRepository propertyPhotoRepository;
    private final MealOrderRepository mealOrderRepository;
    private final PropertyPolicyRepository propertyPolicyRepository;
    private final TaxSettingsRepository taxSettingsRepository;
    private final StorefrontSettingsRepository storefrontSettingsRepository;
    private final FileUploadService fileUploadService;

    public PropertyService(
            PropertyRepository propertyRepository,
            UserRepository userRepository,
            ReservationRepository reservationRepository,
            RoomBlockedDateRepository blockedDateRepository,
            PropertyPhotoRepository propertyPhotoRepository,
            MealOrderRepository mealOrderRepository,
            PropertyPolicyRepository propertyPolicyRepository,
            TaxSettingsRepository taxSettingsRepository,
            StorefrontSettingsRepository storefrontSettingsRepository,
            FileUploadService fileUploadService
    ) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.blockedDateRepository = blockedDateRepository;
        this.propertyPhotoRepository = propertyPhotoRepository;
        this.mealOrderRepository = mealOrderRepository;
        this.propertyPolicyRepository = propertyPolicyRepository;
        this.taxSettingsRepository = taxSettingsRepository;
        this.storefrontSettingsRepository = storefrontSettingsRepository;
        this.fileUploadService = fileUploadService;
    }

    public PropertyResponse createProperty(PropertyRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Property property = new Property();
        property.setUser(user);
        property.setName(request.getName());
        property.setType(request.getType());
        property.setDescription(request.getDescription());
        property.setAddress(request.getAddress());
        property.setCity(request.getCity());
        property.setState(request.getState());
        property.setPostalCode(request.getPostalCode());
        property.setPermitId(request.getPermitId());
        property.setContactEmail(request.getContactEmail());
        property.setContactPhone(request.getContactPhone());

        // Location can just be city, state for quick display
        property.setLocation(request.getCity() + ", " + request.getState());
        
        // Defaults & Wizard Data
        property.setCreatedAt(LocalDateTime.now());
        property.setStatus("Active");
        property.setBaseRate(request.getBaseRate() != null ? request.getBaseRate() : 0);
        property.setIsEntireVilla(request.getIsEntireVilla() != null ? request.getIsEntireVilla() : "Villa".equalsIgnoreCase(request.getType()));
        property.setRooms(request.getRooms() != null ? request.getRooms() : 1);

        if (request.getRoomTypesJson() != null && !request.getRoomTypesJson().isBlank()) {
            property.setRoomTypes(request.getRoomTypesJson());
        }
        if (request.getOperationsJson() != null && !request.getOperationsJson().isBlank()) {
            property.setOperations(request.getOperationsJson());
        }
        if (request.getPropertyAmenitiesJson() != null && !request.getPropertyAmenitiesJson().isBlank()) {
            property.setPropertyAmenities(request.getPropertyAmenitiesJson());
        }

        if (request.getLat() != null) {
            property.setLat(request.getLat());
        }
        if (request.getLng() != null) {
            property.setLng(request.getLng());
        }

        Property savedProperty = propertyRepository.save(property);

        return mapToResponse(savedProperty);
    }

    public List<PropertyResponse> getMyProperties(Long userId) {
        List<Property> properties = propertyRepository.findByUserId(userId);

        return properties.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<PropertyResponse> getAllProperties() {
        List<Property> properties = propertyRepository.findAll();

        return properties.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PropertyResponse getPropertyById(Long id, Long userId) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        if (userId != null && !property.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You do not own this property");
        }
        return mapToResponse(property);
    }

    @Transactional
    public void deleteProperty(Long id, Long userId) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        
        if (!property.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You do not own this property");
        }
        
        List<Reservation> reservations = reservationRepository.findByProperty_Id(id);

        for (Reservation res : reservations) {
            try {
                List<MealOrder> orders = mealOrderRepository.findByReservationIdAndUserId(res.getId(), userId);
                if (orders != null && !orders.isEmpty()) {
                    mealOrderRepository.deleteAll(orders);
                }
            } catch (Exception e) {
                // Ignore if no meal orders exist
            }
        }

        reservationRepository.deleteAll(reservations);
        propertyPhotoRepository.deleteByProperty_Id(id);

        try {
            propertyPolicyRepository.findByPropertyId(id).ifPresent(propertyPolicyRepository::delete);
        } catch (Exception ignored) {}

        try {
            taxSettingsRepository.findByPropertyId(id).ifPresent(taxSettingsRepository::delete);
        } catch (Exception ignored) {}

        propertyRepository.delete(property);
    }

    public String uploadPhoto(Long propertyId, Long userId, MultipartFile file) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        
        if (!property.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You do not own this property");
        }

        String fileUrl = fileUploadService.storeFile(file);

        PropertyPhoto photo = new PropertyPhoto();
        photo.setProperty(property);
        photo.setUrl(fileUrl);
        propertyPhotoRepository.save(photo);

        return fileUrl;
    }

    @Transactional
    public void deletePhoto(Long propertyId, Long userId, String photoUrl) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        
        if (!property.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You do not own this property");
        }

        propertyPhotoRepository.deleteByProperty_IdAndUrl(propertyId, photoUrl);
    }

    public void updateRoomTypes(Long propertyId, Long userId, String roomTypesJson, int totalRoomsCount) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        
        if (!property.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You do not own this property");
        }

        property.setRoomTypes(roomTypesJson);
        property.setRooms(totalRoomsCount);
        propertyRepository.save(property);
    }

    public PropertyResponse updateProperty(Long propertyId, Long userId, stayops_api.dto.PropertyUpdateRequest request) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        
        if (!property.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You do not own this property");
        }

        if (request.getName() != null) property.setName(request.getName());
        if (request.getType() != null) property.setType(request.getType());
        if (request.getDescription() != null) property.setDescription(request.getDescription());
        if (request.getContactEmail() != null) property.setContactEmail(request.getContactEmail());
        if (request.getContactPhone() != null) property.setContactPhone(request.getContactPhone());
        
        if (request.getAddress() != null) property.setAddress(request.getAddress());
        if (request.getCity() != null) property.setCity(request.getCity());
        if (request.getState() != null) property.setState(request.getState());
        if (request.getPostalCode() != null) property.setPostalCode(request.getPostalCode());
        if (request.getLocation() != null && !request.getLocation().isBlank()) {
            property.setLocation(request.getLocation());
        } else if (request.getCity() != null || request.getState() != null) {
            String c = request.getCity() != null ? request.getCity() : property.getCity();
            String s = request.getState() != null ? request.getState() : property.getState();
            property.setLocation((c != null ? c : "") + (c != null && s != null ? ", " : "") + (s != null ? s : ""));
        }
        
        if (request.getStatus() != null) property.setStatus(request.getStatus());
        if (request.getBaseRate() != null) property.setBaseRate(request.getBaseRate());
        
        if (request.getRoomTypes() != null) property.setRoomTypes(request.getRoomTypes());
        if (request.getPropertyAmenities() != null) property.setPropertyAmenities(request.getPropertyAmenities());
        if (request.getPricing() != null) property.setPricing(request.getPricing());
        if (request.getOperations() != null) property.setOperations(request.getOperations());
        
        if (request.getRooms() > 0) {
            property.setRooms(request.getRooms());
        }

        if (request.getLat() != null) {
            property.setLat(request.getLat());
        }
        if (request.getLng() != null) {
            property.setLng(request.getLng());
        }

        property = propertyRepository.save(property);
        return mapToResponse(property);
    }

    public stayops_api.dto.PublicPropertyAvailabilityDTO getPublicAvailability(Long propertyId) {
        List<Reservation> reservations = reservationRepository.findByProperty_Id(propertyId);
        List<stayops_api.entity.RoomBlockedDate> blockedDates = blockedDateRepository.findByPropertyId(propertyId);

        List<stayops_api.dto.PublicPropertyAvailabilityDTO.BookedRange> bookedList = reservations.stream()
                .filter(r -> {
                    String st = r.getStatus() != null ? r.getStatus().toLowerCase() : "";
                    return !st.contains("cancel") && !st.contains("out") && !st.contains("noshow") && !st.contains("no_show");
                })
                .map(r -> new stayops_api.dto.PublicPropertyAvailabilityDTO.BookedRange(
                        r.getRoomUnit(),
                        r.getCheckIn(),
                        r.getCheckOut(),
                        r.getStatus()
                ))
                .toList();

        List<stayops_api.dto.PublicPropertyAvailabilityDTO.BlockedRange> blockedList = blockedDates.stream()
                .map(b -> new stayops_api.dto.PublicPropertyAvailabilityDTO.BlockedRange(
                        b.getRoomId(),
                        b.getStartDate(),
                        b.getEndDate(),
                        b.getCategory()
                ))
                .toList();

        return new stayops_api.dto.PublicPropertyAvailabilityDTO(propertyId, bookedList, blockedList);
    }

    public PropertyResponse getPublicPropertyBySlugOrId(String searchKey) {
        if (searchKey == null || searchKey.isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Property identifier is required"
            );
        }

        List<Property> properties = propertyRepository.findAll();
        String cleanKey = searchKey.trim().toLowerCase().replaceAll("[^a-z0-9]", "");

        // 1. Match by numeric ID (e.g. "33" or "property-33")
        try {
            String numStr = searchKey.toLowerCase().replace("property-", "").replace("property", "").trim();
            Long id = Long.valueOf(numStr);
            for (Property p : properties) {
                if (p.getId().equals(id)) {
                    return mapToResponse(p);
                }
            }
        } catch (NumberFormatException ignored) {}

        // 2. Match by StorefrontSettings subdomain
        if (storefrontSettingsRepository != null && !cleanKey.isEmpty()) {
            java.util.Optional<StorefrontSettings> sfOpt = storefrontSettingsRepository.findBySubdomain(cleanKey);
            if (sfOpt.isPresent() && sfOpt.get().getProperty() != null) {
                return mapToResponse(sfOpt.get().getProperty());
            }
        }

        // 3. Match by property.storefront column (exact match)
        for (Property p : properties) {
            if (p.getStorefront() != null) {
                String cleanSf = p.getStorefront().trim().toLowerCase().replaceAll("[^a-z0-9]", "");
                if (cleanSf.equalsIgnoreCase(cleanKey)) {
                    return mapToResponse(p);
                }
            }
        }

        // 4. Match by exact cleaned property name
        for (Property p : properties) {
            if (p.getName() != null) {
                String cleanName = p.getName().trim().toLowerCase().replaceAll("[^a-z0-9]", "");
                if (cleanName.equalsIgnoreCase(cleanKey)) {
                    return mapToResponse(p);
                }
            }
        }

        // Strict Tenant Isolation: Never leak another tenant's property details
        throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Property not found for identifier: " + searchKey
        );
    }

    private PropertyResponse mapToResponse(Property property) {
        return new PropertyResponse(
                property.getId(),
                property.getName(),
                property.getType(),
                property.getLocation(),
                property.getDescription(),
                property.getAddress(),
                property.getCity(),
                property.getState(),
                property.getPostalCode(),
                property.getPermitId(),
                property.getContactEmail(),
                property.getContactPhone(),
                property.getRooms(),
                property.getStatus(),
                property.getBaseRate(),
                property.getIsEntireVilla(),
                propertyPhotoRepository.findByProperty_Id(property.getId())
                        .stream().map(PropertyPhoto::getUrl).collect(Collectors.toList()),
                property.getRoomTypes(),
                property.getPropertyAmenities(),
                property.getPricing(),
                property.getOperations(),
                property.getLat(),
                property.getLng()
        );
    }
}