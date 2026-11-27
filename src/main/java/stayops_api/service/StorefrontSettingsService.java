package stayops_api.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import stayops_api.dto.StorefrontSettingsDTO;
import stayops_api.entity.Property;
import stayops_api.entity.StorefrontSettings;
import stayops_api.repository.PropertyRepository;
import stayops_api.repository.StorefrontSettingsRepository;

import java.util.Optional;

@Service
public class StorefrontSettingsService {

    private final StorefrontSettingsRepository storefrontSettingsRepository;
    private final PropertyRepository propertyRepository;
    private final FileUploadService fileUploadService;

    public StorefrontSettingsService(
            StorefrontSettingsRepository storefrontSettingsRepository,
            PropertyRepository propertyRepository,
            FileUploadService fileUploadService
    ) {
        this.storefrontSettingsRepository = storefrontSettingsRepository;
        this.propertyRepository = propertyRepository;
        this.fileUploadService = fileUploadService;
    }

    @Transactional(readOnly = true)
    public StorefrontSettingsDTO getSettingsByProperty(Long propertyId, Long userId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));

        if (!property.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized: You do not own this property");
        }

        StorefrontSettings settings = storefrontSettingsRepository.findByPropertyId(propertyId)
                .orElseGet(() -> createDefaultSettings(property));

        return mapToDTO(settings);
    }

    @Transactional(readOnly = true)
    public StorefrontSettingsDTO getPublicSettingsByProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));

        StorefrontSettings settings = storefrontSettingsRepository.findByPropertyId(propertyId)
                .orElseGet(() -> createDefaultSettings(property));

        return mapToDTO(settings);
    }

    private static final java.util.Set<String> RESERVED_SUBDOMAINS = java.util.Set.of(
            "api", "admin", "app", "auth", "dashboard", "root", "static", "support",
            "stayops", "null", "undefined", "localhost", "www", "mail", "help",
            "settings", "login", "signup", "register", "booking", "checkout", "public",
            "assets", "system", "internal", "config", "test"
    );

    private static final java.util.regex.Pattern SUBDOMAIN_PATTERN =
            java.util.regex.Pattern.compile("^[a-z0-9]+(-[a-z0-9]+)*$");

    @Transactional(readOnly = true)
    public StorefrontSettingsDTO getPublicSettingsBySubdomain(String subdomain) {
        if (subdomain == null || subdomain.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subdomain parameter is required");
        }

        String cleanSubdomain = subdomain.trim().toLowerCase();

        // Try finding by exact subdomain
        Optional<StorefrontSettings> settingsOpt = storefrontSettingsRepository.findBySubdomain(cleanSubdomain);
        if (settingsOpt.isPresent()) {
            return mapToDTO(settingsOpt.get());
        }

        // Fallback: If numerical string, search by Property ID
        try {
            Long propId = Long.parseLong(cleanSubdomain);
            return getPublicSettingsByProperty(propId);
        } catch (NumberFormatException ignored) {}

        // Strict Tenant Isolation: Never leak another tenant's settings on missing/invalid subdomain
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Storefront not found for subdomain: " + subdomain);
    }

    @Transactional
    public StorefrontSettingsDTO saveSettingsByProperty(Long propertyId, Long userId, StorefrontSettingsDTO dto) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));

        if (!property.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized: You do not own this property");
        }

        StorefrontSettings settings = storefrontSettingsRepository.findByPropertyId(propertyId)
                .orElseGet(() -> createDefaultSettings(property));

        if (dto.getSubdomain() != null && !dto.getSubdomain().trim().isEmpty()) {
            String rawSlug = dto.getSubdomain().trim().toLowerCase();
            String cleanSlug = rawSlug.replaceAll("[^a-z0-9-]", "").replaceAll("^-+|-+$", "");

            if (cleanSlug.length() < 3 || cleanSlug.length() > 63) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subdomain slug must be between 3 and 63 characters");
            }

            if (!SUBDOMAIN_PATTERN.matcher(cleanSlug).matches()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subdomain slug may only contain lowercase alphanumeric characters and hyphens");
            }

            if (RESERVED_SUBDOMAINS.contains(cleanSlug)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subdomain slug '" + cleanSlug + "' is reserved and cannot be used");
            }
            
            // Ensure subdomain is unique if changed
            Optional<StorefrontSettings> existingSub = storefrontSettingsRepository.findBySubdomain(cleanSlug);
            if (existingSub.isPresent() && !existingSub.get().getProperty().getId().equals(propertyId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subdomain slug '" + cleanSlug + "' is already taken by another property");
            }
            settings.setSubdomain(cleanSlug);
            property.setStorefront(cleanSlug);
        }

        if (dto.getPreset() != null) {
            settings.setPreset(dto.getPreset());
        }

        if (dto.getPrimaryColor() != null) {
            settings.setPrimaryColor(dto.getPrimaryColor());
        }

        if (dto.getSecondaryColor() != null) {
            settings.setSecondaryColor(dto.getSecondaryColor());
        }

        if (dto.getTagline() != null) {
            settings.setTagline(dto.getTagline());
        }

        StorefrontSettings saved = storefrontSettingsRepository.save(settings);
        propertyRepository.save(property);

        return mapToDTO(saved);
    }

    @Transactional
    public String uploadImageForProperty(Long propertyId, Long userId, MultipartFile file) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));

        if (!property.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized: You do not own this property");
        }

        return fileUploadService.storeImageFile(file);
    }

    private StorefrontSettings createDefaultSettings(Property property) {
        String defaultSlug = (property.getName() != null ? property.getName() : "property")
                .toLowerCase().replaceAll("[^a-z0-9]", "");
        if (defaultSlug.isEmpty()) defaultSlug = "prop" + property.getId();

        return new StorefrontSettings(
                property,
                defaultSlug,
                "slate",
                "#111827",
                "#C5A059",
                "Several Cottages — One Host, Many Places to Stay"
        );
    }

    private StorefrontSettingsDTO mapToDTO(StorefrontSettings settings) {
        return new StorefrontSettingsDTO(
                settings.getId(),
                settings.getProperty().getId(),
                settings.getSubdomain(),
                settings.getPreset(),
                settings.getPrimaryColor(),
                settings.getSecondaryColor(),
                settings.getTagline()
        );
    }
}
