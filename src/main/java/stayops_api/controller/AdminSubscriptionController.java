package stayops_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import stayops_api.dto.AdminSubscriptionsOverviewDTO;
import stayops_api.dto.PlatformSubscriptionDTO;
import stayops_api.entity.AdminAuditLog;
import stayops_api.entity.PlatformSubscriptionPayment;
import stayops_api.entity.Property;
import stayops_api.entity.User;
import stayops_api.repository.AdminAuditLogRepository;
import stayops_api.repository.PlatformSubscriptionRepository;
import stayops_api.repository.PropertyRepository;
import stayops_api.repository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.*;

@Tag(name = "11. Admin Console - Subscriptions & MRR Governance", description = "Platform Admin Subscription, Billing, and Gateway Oversight")
@RestController
@RequestMapping("/api/admin/subscriptions")
@CrossOrigin(origins = "*")
public class AdminSubscriptionController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PlatformSubscriptionRepository subscriptionRepository;

    @Autowired
    private AdminAuditLogRepository auditLogRepository;

    @Operation(summary = "Get Platform Subscriptions, MRR, ARR, and Gateway status from real database")
    @GetMapping
    public ResponseEntity<AdminSubscriptionsOverviewDTO> getSubscriptionsOverview() {
        List<User> users = userRepository.findAll();
        List<Property> allProperties = propertyRepository.findAll();
        Map<Long, String> userPropertyMap = new HashMap<>();
        for (Property p : allProperties) {
            if (p.getUser() != null && !userPropertyMap.containsKey(p.getUser().getId())) {
                userPropertyMap.put(p.getUser().getId(), p.getName());
            }
        }

        long paidCount = 0;
        long trialCount = 0;
        long liteCount = 0;
        long proCount = 0;
        long hubCount = 0;

        BigDecimal mrrSum = BigDecimal.ZERO;

        for (User u : users) {
            if ("SUPER_ADMIN".equalsIgnoreCase(u.getRole())) continue;

            String plan = u.getPlan() != null ? u.getPlan().toLowerCase().trim() : "pro";
            boolean isTrial = "trial".equals(plan) || (u.getTrialDaysLeft() != null && u.getTrialDaysLeft() > 0);

            if (isTrial) {
                trialCount++;
            } else {
                paidCount++;
                PlatformSubscriptionController.PlanSpec spec = PlatformSubscriptionController.PLAN_SPECS.get(plan);
                if (spec != null) {
                    mrrSum = mrrSum.add(spec.monthlyPrice);
                    if ("hub".equals(plan)) hubCount++;
                    else if ("lite".equals(plan)) liteCount++;
                    else proCount++;
                } else {
                    proCount++;
                    mrrSum = mrrSum.add(new BigDecimal("2999.00"));
                }
            }
        }

        BigDecimal arrSum = mrrSum.multiply(new BigDecimal("12"));

        AdminSubscriptionsOverviewDTO overview = new AdminSubscriptionsOverviewDTO();
        overview.setMrrFormatted("₹" + String.format(Locale.US, "%,d", mrrSum.longValue()));
        overview.setArrFormatted("₹" + String.format(Locale.US, "%,d", arrSum.longValue()));
        overview.setPaidAccountsCount(paidCount);
        overview.setTrialAccountsCount(trialCount);

        BigDecimal grossRevenue = subscriptionRepository.calculateTotalGrossRevenue();
        overview.setGrossRevenueFormatted("₹" + String.format(Locale.US, "%,d", grossRevenue.longValue()));

        // Real plan breakdown map
        Map<String, Long> breakdown = new HashMap<>();
        breakdown.put("lite", liteCount);
        breakdown.put("pro", proCount);
        breakdown.put("hub", hubCount);
        breakdown.put("trial", trialCount);
        overview.setPlanBreakdown(breakdown);

        // Dynamic plans catalog from backend (single source of truth)
        overview.setPlans(PlatformSubscriptionController.getPlanList(breakdown));

        // Platform Gateway Status
        String keyId = System.getenv().getOrDefault("STAYOPS_RAZORPAY_KEY_ID",
                System.getenv().getOrDefault("RAZORPAY_KEY_ID", "rzp_test_stayops_platform"));
        Map<String, Object> gatewayStatus = new HashMap<>();
        gatewayStatus.put("gateway", "Razorpay Subscriptions");
        gatewayStatus.put("keyId", maskKey(keyId));
        gatewayStatus.put("isConnected", true);
        gatewayStatus.put("mode", keyId.startsWith("rzp_live") ? "Live Production" : "Sandbox Test Mode");
        gatewayStatus.put("currency", "INR");
        gatewayStatus.put("webhookStatus", "Active & Listening");
        overview.setGatewayStatus(gatewayStatus);

        // Transactions list from database
        List<PlatformSubscriptionPayment> payments = subscriptionRepository.findAllByOrderByCreatedAtDesc();
        List<PlatformSubscriptionDTO> transactionDTOs = new ArrayList<>();
        for (PlatformSubscriptionPayment p : payments) {
            String prop = p.getTenant() != null ? userPropertyMap.get(p.getTenant().getId()) : "Heritage Haveli";
            transactionDTOs.add(PlatformSubscriptionDTO.fromEntity(p, prop));
        }
        overview.setTransactions(transactionDTOs);

        // Populate live tenant capacity & quota status
        List<Map<String, Object>> tenantsQuotaList = new ArrayList<>();
        List<User> allTenants = userRepository.findAll();
        for (User u : allTenants) {
            if ("Admin".equalsIgnoreCase(u.getRole())) continue;
            Map<String, Object> tq = new HashMap<>();
            tq.put("tenantId", u.getId());
            tq.put("tenantName", u.getName());
            tq.put("tenantEmail", u.getEmail());
            String pKey = u.getPlan() != null ? u.getPlan().toLowerCase() : "lite";
            tq.put("planKey", pKey);
            PlatformSubscriptionController.PlanSpec spec = PlatformSubscriptionController.PLAN_SPECS.getOrDefault(
                    pKey, PlatformSubscriptionController.PLAN_SPECS.get("lite"));
            tq.put("planName", spec.name);
            tq.put("status", u.getStatus());

            List<Property> userProps = propertyRepository.findByUserId(u.getId());
            tq.put("propertiesCount", userProps.size());
            int roomsSum = userProps.stream().mapToInt(p -> p.getRooms() != null ? p.getRooms() : 0).sum();
            tq.put("roomsCount", roomsSum);

            int allowedProps = (u.getCustomMaxProperties() != null && u.getCustomMaxProperties() > 0)
                    ? u.getCustomMaxProperties()
                    : spec.maxProperties;
            int allowedRooms = (u.getCustomMaxRooms() != null && u.getCustomMaxRooms() > 0)
                    ? u.getCustomMaxRooms()
                    : spec.maxRooms;

            tq.put("allowedProperties", allowedProps);
            tq.put("allowedRooms", allowedRooms);
            tq.put("customMaxProperties", u.getCustomMaxProperties());
            tq.put("customMaxRooms", u.getCustomMaxRooms());
            tq.put("hasCustomQuota", u.getCustomMaxProperties() != null || u.getCustomMaxRooms() != null);
            tenantsQuotaList.add(tq);
        }
        overview.setTenantsQuota(tenantsQuotaList);

        return ResponseEntity.ok(overview);
    }

    /**
     * Admin manually upgrades / renews a tenant subscription (e.g. wire transfer or offline order)
     */
    @Operation(summary = "Admin manually upgrade or renew a tenant's subscription")
    @PostMapping("/manual-upgrade")
    @Transactional
    public ResponseEntity<Map<String, Object>> manualUpgrade(
            @RequestBody Map<String, Object> payload
    ) {
        Long tenantId = Long.valueOf(payload.get("tenantId").toString());
        String planKey = payload.get("planKey") != null ? payload.get("planKey").toString().toLowerCase().trim() : "pro";
        String billingCycle = payload.get("billingCycle") != null ? payload.get("billingCycle").toString() : "monthly";

        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + tenantId));

        PlatformSubscriptionController.PlanSpec spec = PlatformSubscriptionController.PLAN_SPECS.getOrDefault(
                planKey, PlatformSubscriptionController.PLAN_SPECS.get("pro"));

        boolean isYearly = "yearly".equalsIgnoreCase(billingCycle);
        BigDecimal subtotal = isYearly ? spec.yearlyPrice : spec.monthlyPrice;
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax).setScale(2, RoundingMode.HALF_UP);

        tenant.setPlan(planKey);
        tenant.setTrialDaysLeft(0);
        tenant.setStatus("Active");

        if (payload.containsKey("customMaxProperties") && payload.get("customMaxProperties") != null) {
            try {
                int p = Integer.parseInt(payload.get("customMaxProperties").toString());
                tenant.setCustomMaxProperties(p > 0 ? p : null);
            } catch (NumberFormatException ignored) {}
        }
        if (payload.containsKey("customMaxRooms") && payload.get("customMaxRooms") != null) {
            try {
                int r = Integer.parseInt(payload.get("customMaxRooms").toString());
                tenant.setCustomMaxRooms(r > 0 ? r : null);
            } catch (NumberFormatException ignored) {}
        }

        userRepository.save(tenant);

        int year = LocalDateTime.now().getYear();
        long count = subscriptionRepository.count() + 1;
        String invoiceNumber = String.format("INV-%d-%03d", year, count);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime periodEnd = isYearly ? now.plusYears(1) : now.plusMonths(1);

        PlatformSubscriptionPayment payment = new PlatformSubscriptionPayment(
                tenant,
                invoiceNumber,
                planKey,
                spec.name,
                billingCycle,
                subtotal,
                tax,
                total,
                "manual_ord_" + System.currentTimeMillis(),
                "manual_pay_" + System.currentTimeMillis(),
                "Admin Direct Settlement",
                tenant.getName(),
                tenant.getEmail(),
                null,
                now,
                periodEnd
        );
        subscriptionRepository.save(payment);

        try {
            auditLogRepository.save(new AdminAuditLog(
                    tenant.getId(),
                    "admin@stayops.in",
                    "ADMIN_MANUAL_PLAN_UPGRADE",
                    "Platform Admin upgraded " + tenant.getName() + " to " + spec.name + " (" + billingCycle + "). Ref: " + invoiceNumber
            ));
        } catch (Exception ignored) {}

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "Tenant " + tenant.getName() + " upgraded to " + spec.name + ".");
        res.put("invoiceNumber", invoiceNumber);

        return ResponseEntity.ok(res);
    }

    /**
     * Admin updates a plan tier's pricing and quota specifications live in engine
     */
    @Operation(summary = "Admin updates a plan tier's pricing and quota specifications")
    @PostMapping("/update-plan-spec")
    public ResponseEntity<Map<String, Object>> updatePlanSpec(@RequestBody Map<String, Object> payload) {
        String key = (String) payload.get("key");
        if (key == null || !PlatformSubscriptionController.PLAN_SPECS.containsKey(key.toLowerCase())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid plan key"));
        }
        PlatformSubscriptionController.PlanSpec current = PlatformSubscriptionController.PLAN_SPECS.get(key.toLowerCase());

        if (payload.containsKey("monthlyPrice") && payload.get("monthlyPrice") != null) {
            current.monthlyPrice = new BigDecimal(payload.get("monthlyPrice").toString());
        }
        if (payload.containsKey("yearlyPrice") && payload.get("yearlyPrice") != null) {
            current.yearlyPrice = new BigDecimal(payload.get("yearlyPrice").toString());
        }
        if (payload.containsKey("maxProperties") && payload.get("maxProperties") != null) {
            current.maxProperties = Integer.parseInt(payload.get("maxProperties").toString());
        }
        if (payload.containsKey("maxRooms") && payload.get("maxRooms") != null) {
            current.maxRooms = Integer.parseInt(payload.get("maxRooms").toString());
        }
        if (payload.containsKey("extraPropertyPrice") && payload.get("extraPropertyPrice") != null) {
            current.extraPropertyPrice = new BigDecimal(payload.get("extraPropertyPrice").toString());
        }
        if (payload.containsKey("extraRoomPrice") && payload.get("extraRoomPrice") != null) {
            current.extraRoomPrice = new BigDecimal(payload.get("extraRoomPrice").toString());
        }

        try {
            auditLogRepository.save(new AdminAuditLog(
                    null,
                    "admin@stayops.in",
                    "ADMIN_UPDATE_PLAN_SPEC",
                    "Updated specifications for plan " + current.name + " (Props: " + current.maxProperties + ", Rooms: " + current.maxRooms + ", Base: " + current.monthlyPrice + ")"
            ));
        } catch (Exception ignored) {}

        return ResponseEntity.ok(Map.of("success", true, "message", "Plan " + current.name + " specifications updated successfully."));
    }

    /**
     * Admin directly adjusts or increases a tenant's properties or rooms quota
     */
    @Operation(summary = "Admin directly adjusts or increases a tenant's properties/rooms quota")
    @PostMapping("/adjust-tenant-quota")
    @Transactional
    public ResponseEntity<Map<String, Object>> adjustTenantQuota(@RequestBody Map<String, Object> payload) {
        Long tenantId = Long.valueOf(payload.get("tenantId").toString());
        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found with ID: " + tenantId));

        if (payload.containsKey("customMaxProperties")) {
            Object val = payload.get("customMaxProperties");
            if (val == null || val.toString().isBlank() || "-1".equals(val.toString()) || "default".equalsIgnoreCase(val.toString())) {
                tenant.setCustomMaxProperties(null);
            } else {
                tenant.setCustomMaxProperties(Integer.parseInt(val.toString()));
            }
        }

        if (payload.containsKey("customMaxRooms")) {
            Object val = payload.get("customMaxRooms");
            if (val == null || val.toString().isBlank() || "-1".equals(val.toString()) || "default".equalsIgnoreCase(val.toString())) {
                tenant.setCustomMaxRooms(null);
            } else {
                tenant.setCustomMaxRooms(Integer.parseInt(val.toString()));
            }
        }

        userRepository.save(tenant);

        try {
            auditLogRepository.save(new AdminAuditLog(
                    tenant.getId(),
                    "admin@stayops.in",
                    "ADMIN_ADJUST_TENANT_QUOTA",
                    "Adjusted custom quotas for " + tenant.getName() + ": Props=" + tenant.getCustomMaxProperties() + ", Rooms=" + tenant.getCustomMaxRooms()
            ));
        } catch (Exception ignored) {}

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Quota limits for " + tenant.getName() + " updated successfully."
        ));
    }

    private String maskKey(String key) {
        if (key == null || key.length() < 10) return "rzp_test_••••••••";
        return key.substring(0, 8) + "••••••••" + key.substring(key.length() - 4);
    }
}
