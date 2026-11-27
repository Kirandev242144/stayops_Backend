package stayops_api.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp", ".avif");
    private static final Set<String> ALLOWED_IMAGE_MIME_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/avif");

    private static final Set<String> ALLOWED_DOC_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp", ".avif", ".pdf");
    private static final Set<String> ALLOWED_DOC_MIME_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/avif", "application/pdf");

    private final Path uploadLocation;

    public FileUploadService() {
        this.uploadLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    /**
     * Store an image file with strict image whitelisting (JPEG, PNG, WEBP, AVIF).
     */
    public String storeImageFile(MultipartFile file) {
        return storeValidatedFile(file, ALLOWED_IMAGE_EXTENSIONS, ALLOWED_IMAGE_MIME_TYPES, "Image");
    }

    /**
     * Store a general file (images + PDF documents for guest ID verification).
     */
    public String storeFile(MultipartFile file) {
        return storeValidatedFile(file, ALLOWED_DOC_EXTENSIONS, ALLOWED_DOC_MIME_TYPES, "Document/Image");
    }

    private String storeValidatedFile(
            MultipartFile file,
            Set<String> allowedExtensions,
            Set<String> allowedMimeTypes,
            String fileCategoryLabel
    ) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot upload an empty file");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size exceeds the 10MB limit");
        }

        String contentType = file.getContentType();
        if (contentType == null || !allowedMimeTypes.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Unsupported file type: " + contentType + ". Allowed types for " + fileCategoryLabel + ": " + allowedMimeTypes);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File name cannot be empty");
        }

        String cleanedName = org.springframework.util.StringUtils.cleanPath(originalFilename);
        if (cleanedName.contains("..") || cleanedName.contains("/") || cleanedName.contains("\\")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file path characters in filename");
        }

        String fileExtension = "";
        int dotIndex = cleanedName.lastIndexOf(".");
        if (dotIndex >= 0) {
            fileExtension = cleanedName.substring(dotIndex).toLowerCase(Locale.ROOT);
        }

        if (!allowedExtensions.contains(fileExtension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Unsupported file extension: '" + fileExtension + "'. Allowed extensions: " + allowedExtensions);
        }

        try {
            String newFileName = UUID.randomUUID().toString() + fileExtension;
            Path targetLocation = this.uploadLocation.resolve(newFileName).normalize();

            // Guard against directory traversal
            if (!targetLocation.startsWith(this.uploadLocation)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot store file outside current directory");
            }

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + newFileName;
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store file: " + ex.getMessage(), ex);
        }
    }
}
