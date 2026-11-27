package stayops_api.service;

import org.springframework.stereotype.Service;
import stayops_api.dto.*;
import stayops_api.entity.GuestProfile;
import stayops_api.entity.Reservation;
import stayops_api.entity.User;
import stayops_api.repository.GuestProfileRepository;
import stayops_api.repository.ReservationRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class GuestProfileService {

    private final GuestProfileRepository guestProfileRepository;
    private final ReservationRepository reservationRepository;

    public GuestProfileService(
            GuestProfileRepository guestProfileRepository,
            ReservationRepository reservationRepository
    ) {
        this.guestProfileRepository = guestProfileRepository;
        this.reservationRepository = reservationRepository;
    }

    public void syncGuestProfile(String name, String email, String phone, User user) {
        syncGuestProfile(name, email, phone, null, null, null, null, user);
    }

    public void syncGuestProfile(
            String name,
            String email,
            String phone,
            String idType,
            String idNumber,
            String idDocumentUrl,
            String idDocuments,
            User user
    ) {
        GuestProfile profile = guestProfileRepository.findByPhoneAndUserId(phone, user.getId()).orElse(null);

        if (profile == null) {
            profile = new GuestProfile();
            profile.setName(name);
            profile.setEmail(email);
            profile.setPhone(phone);
            profile.setIdType(idType);
            profile.setIdNumber(idNumber);
            profile.setIdDocumentUrl(idDocumentUrl);
            profile.setIdDocuments(idDocuments);
            profile.setUser(user);
            guestProfileRepository.save(profile);
        } else {
            boolean updated = false;
            if (name != null && !name.trim().isEmpty() && !name.equals(profile.getName())) {
                profile.setName(name);
                updated = true;
            }
            if (email != null && !email.trim().isEmpty() && !email.equals(profile.getEmail())) {
                profile.setEmail(email);
                updated = true;
            }
            if (idType != null && !idType.trim().isEmpty()) {
                profile.setIdType(idType);
                updated = true;
            }
            if (idNumber != null && !idNumber.trim().isEmpty()) {
                profile.setIdNumber(idNumber);
                updated = true;
            }
            if (idDocumentUrl != null && !idDocumentUrl.trim().isEmpty()) {
                profile.setIdDocumentUrl(idDocumentUrl);
                updated = true;
            }
            if (idDocuments != null && !idDocuments.trim().isEmpty()) {
                profile.setIdDocuments(idDocuments);
                updated = true;
            }
            if (updated) {
                guestProfileRepository.save(profile);
            }
        }
    }

    public GuestProfileResponse createGuestProfile(String name, String email, String phone, String idNumber, Long userId) {
        return createGuestProfile(name, email, phone, null, idNumber, null, null, userId);
    }

    public GuestProfileResponse createGuestProfile(
            String name,
            String email,
            String phone,
            String idType,
            String idNumber,
            String idDocumentUrl,
            String idDocuments,
            Long userId
    ) {
        User user = new User();
        user.setId(userId);

        GuestProfile profile = new GuestProfile();
        profile.setName(name);
        profile.setEmail(email);
        profile.setPhone(phone);
        profile.setIdType(idType);
        profile.setIdNumber(idNumber);
        profile.setIdDocumentUrl(idDocumentUrl);
        profile.setIdDocuments(idDocuments);
        profile.setUser(user);
        
        GuestProfile saved = guestProfileRepository.save(profile);
        return convertToResponse(saved);
    }

    public List<GuestCrmSummary> getAllGuestSummaries(Long userId) {
        List<GuestProfile> profiles = guestProfileRepository.findByUserId(userId);

        return profiles.stream().map(profile -> {
            List<Reservation> reservations = reservationRepository.findByGuestPhoneAndUser_IdOrderByCheckInDesc(profile.getPhone(), userId);
            
            // Auto-heal missing ID information from guest's reservation history
            boolean profileUpdated = false;
            for (Reservation r : reservations) {
                if ((profile.getIdType() == null || profile.getIdType().isBlank()) && r.getIdType() != null && !r.getIdType().isBlank()) {
                    profile.setIdType(r.getIdType());
                    profileUpdated = true;
                }
                if ((profile.getIdNumber() == null || profile.getIdNumber().isBlank()) && r.getIdNumber() != null && !r.getIdNumber().isBlank()) {
                    profile.setIdNumber(r.getIdNumber());
                    profileUpdated = true;
                }
                if ((profile.getIdDocumentUrl() == null || profile.getIdDocumentUrl().isBlank()) && r.getIdDocumentUrl() != null && !r.getIdDocumentUrl().isBlank()) {
                    profile.setIdDocumentUrl(r.getIdDocumentUrl());
                    profileUpdated = true;
                }
                if ((profile.getIdDocuments() == null || profile.getIdDocuments().isBlank()) && r.getIdDocuments() != null && !r.getIdDocuments().isBlank()) {
                    profile.setIdDocuments(r.getIdDocuments());
                    profileUpdated = true;
                }
            }
            if (profileUpdated) {
                guestProfileRepository.save(profile);
            }

            Integer totalStays = reservations.size();
            BigDecimal totalSpend = reservations.stream()
                    .map(Reservation::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            LocalDate lastStayDate = reservations.isEmpty() ? null : reservations.get(0).getCheckIn();

            return new GuestCrmSummary(
                    convertToResponse(profile),
                    totalStays,
                    totalSpend,
                    lastStayDate
            );
        }).toList();
    }

    public GuestProfileDetailResponse getGuestProfileDetail(Long profileId, Long userId) {
        GuestProfile profile = guestProfileRepository.findById(profileId)
                .filter(p -> p.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Guest Profile not found"));

        List<Reservation> reservations = reservationRepository.findByGuestPhoneAndUser_IdOrderByCheckInDesc(profile.getPhone(), userId);

        // Auto-heal missing ID information from guest's reservation history
        boolean profileUpdated = false;
        for (Reservation r : reservations) {
            if ((profile.getIdType() == null || profile.getIdType().isBlank()) && r.getIdType() != null && !r.getIdType().isBlank()) {
                profile.setIdType(r.getIdType());
                profileUpdated = true;
            }
            if ((profile.getIdNumber() == null || profile.getIdNumber().isBlank()) && r.getIdNumber() != null && !r.getIdNumber().isBlank()) {
                profile.setIdNumber(r.getIdNumber());
                profileUpdated = true;
            }
            if ((profile.getIdDocumentUrl() == null || profile.getIdDocumentUrl().isBlank()) && r.getIdDocumentUrl() != null && !r.getIdDocumentUrl().isBlank()) {
                profile.setIdDocumentUrl(r.getIdDocumentUrl());
                profileUpdated = true;
            }
            if ((profile.getIdDocuments() == null || profile.getIdDocuments().isBlank()) && r.getIdDocuments() != null && !r.getIdDocuments().isBlank()) {
                profile.setIdDocuments(r.getIdDocuments());
                profileUpdated = true;
            }
        }
        if (profileUpdated) {
            profile = guestProfileRepository.save(profile);
        }

        Integer totalStays = reservations.size();
        BigDecimal totalSpend = reservations.stream()
                .map(Reservation::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        LocalDate lastStayDate = reservations.isEmpty() ? null : reservations.get(0).getCheckIn();

        List<ReservationResponse> reservationResponses = reservations.stream()
                .map(this::convertReservationToResponse)
                .toList();

        return new GuestProfileDetailResponse(
                convertToResponse(profile),
                totalStays,
                totalSpend,
                lastStayDate,
                reservationResponses
        );
    }

    public GuestProfileResponse updateGuestProfile(Long profileId, Long userId, GuestProfileUpdateRequest request) {
        GuestProfile profile = guestProfileRepository.findById(profileId)
                .filter(p -> p.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Guest Profile not found"));

        if (request.getName() != null) profile.setName(request.getName());
        if (request.getEmail() != null) profile.setEmail(request.getEmail());
        if (request.getIdType() != null) profile.setIdType(request.getIdType());
        if (request.getIdNumber() != null) profile.setIdNumber(request.getIdNumber());
        if (request.getIdDocumentUrl() != null) profile.setIdDocumentUrl(request.getIdDocumentUrl());
        if (request.getIdDocuments() != null) profile.setIdDocuments(request.getIdDocuments());
        if (request.getPrivateNotes() != null) profile.setPrivateNotes(request.getPrivateNotes());

        GuestProfile saved = guestProfileRepository.save(profile);
        return convertToResponse(saved);
    }

    private GuestProfileResponse convertToResponse(GuestProfile profile) {
        return new GuestProfileResponse(
                profile.getId(),
                profile.getName(),
                profile.getPhone(),
                profile.getEmail(),
                profile.getIdType(),
                profile.getIdNumber(),
                profile.getIdDocumentUrl(),
                profile.getIdDocuments(),
                profile.getPrivateNotes(),
                profile.getCreatedAt()
        );
    }

    private ReservationResponse convertReservationToResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getBookingRef(),
                reservation.getGuestName(),
                reservation.getGuestEmail(),
                reservation.getGuestPhone(),
                reservation.getProperty().getName(),
                reservation.getRoomUnit(),
                reservation.getCheckIn(),
                reservation.getCheckOut(),
                reservation.getGuestCount(),
                reservation.getTotalAmount(),
                reservation.getPaymentStatus(),
                reservation.getAmountPaid(),
                reservation.getBookingSource(),
                reservation.getNotes(),
                reservation.getRazorpayPaymentId(),
                reservation.getStatus(),
                reservation.getCreatedAt()
        );
    }
}
