package stayops_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import stayops_api.dto.PropertyPolicyDTO;
import stayops_api.entity.Property;
import stayops_api.entity.PropertyPolicy;
import stayops_api.repository.PropertyPolicyRepository;
import stayops_api.repository.PropertyRepository;

@Service
public class PropertyPolicyService {

    private final PropertyPolicyRepository propertyPolicyRepository;
    private final PropertyRepository propertyRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PropertyPolicyService(
            PropertyPolicyRepository propertyPolicyRepository,
            PropertyRepository propertyRepository
    ) {
        this.propertyPolicyRepository = propertyPolicyRepository;
        this.propertyRepository = propertyRepository;
    }

    @Transactional(readOnly = true)
    public PropertyPolicyDTO getPolicyByProperty(Long propertyId, Long userId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));

        if (!property.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized: You do not own this property");
        }

        PropertyPolicy policy = propertyPolicyRepository.findByPropertyId(propertyId)
                .orElseGet(() -> new PropertyPolicy(property));

        return mapToDTO(policy);
    }

    @Transactional(readOnly = true)
    public PropertyPolicyDTO getPublicPolicyByProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));

        PropertyPolicy policy = propertyPolicyRepository.findByPropertyId(propertyId)
                .orElseGet(() -> new PropertyPolicy(property));

        return mapToDTO(policy);
    }

    @Transactional
    public PropertyPolicyDTO savePolicyByProperty(Long propertyId, Long userId, PropertyPolicyDTO dto) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));

        if (!property.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized: You do not own this property");
        }

        PropertyPolicy policy = propertyPolicyRepository.findByPropertyId(propertyId)
                .orElseGet(() -> new PropertyPolicy(property));

        if (dto.getNoCancellationPolicy() != null) {
            policy.setNoCancellationPolicy(dto.getNoCancellationPolicy());
        }
        if (dto.getCancellationRulesJson() != null) {
            policy.setCancellationRulesJson(dto.getCancellationRulesJson());
        }
        if (dto.getTermsAndConditions() != null) {
            policy.setTermsAndConditions(dto.getTermsAndConditions());
        }
        if (dto.getPrivacyPolicy() != null) {
            policy.setPrivacyPolicy(dto.getPrivacyPolicy());
        }
        if (dto.getRefundPolicy() != null) {
            policy.setRefundPolicy(dto.getRefundPolicy());
        }
        if (dto.getChildAgePolicyJson() != null) {
            policy.setChildAgePolicyJson(dto.getChildAgePolicyJson());
        }

        PropertyPolicy saved = propertyPolicyRepository.save(policy);
        syncOperationsCancellationPolicy(property, saved);
        return mapToDTO(saved);
    }

    private void syncOperationsCancellationPolicy(Property property, PropertyPolicy policy) {
        try {
            String summary = "Cancellation policy not configured";
            if (Boolean.TRUE.equals(policy.getNoCancellationPolicy())) {
                summary = "No cancellation policy (100% Free cancellation)";
            } else if (policy.getCancellationRulesJson() != null) {
                summary = evaluateCancellationSummaryString(policy.getCancellationRulesJson());
            }

            String opsStr = property.getOperations();
            ObjectNode opsNode;
            if (opsStr != null && !opsStr.isBlank()) {
                opsNode = (ObjectNode) objectMapper.readTree(opsStr);
            } else {
                opsNode = objectMapper.createObjectNode();
            }
            opsNode.put("cancellationPolicy", summary);
            property.setOperations(objectMapper.writeValueAsString(opsNode));
            propertyRepository.save(property);
        } catch (Exception e) {
            // Ignore ops sync warning
        }
    }

    private String evaluateCancellationSummaryString(String jsonStr) {
        try {
            JsonNode root = objectMapper.readTree(jsonStr);
            if (root.isArray() && root.size() > 0) {
                java.util.List<JsonNode> activeRules = new java.util.ArrayList<>();
                for (JsonNode node : root) {
                    if (!node.has("active") || node.get("active").asBoolean()) {
                        activeRules.add(node);
                    }
                }
                if (activeRules.isEmpty()) {
                    return "Cancellation policy not configured";
                }
                if (activeRules.size() == 1) {
                    JsonNode first = activeRules.get(0);
                    int val = first.has("chargeValue") ? first.get("chargeValue").asInt() : (first.has("deductValue") ? first.get("deductValue").asInt() : 100);
                    int unit = first.has("beforeValue") ? first.get("beforeValue").asInt() : 1;
                    String beforeUnit = first.has("beforeUnit") ? first.get("beforeUnit").asText() : "Day";
                    String type = first.has("chargeType") ? first.get("chargeType").asText() : "PERCENTAGE";
                    String ruleType = first.has("type") ? first.get("type").asText() : "BEFORE_CHECKIN";

                    String chargeUnitStr = "NIGHTS".equalsIgnoreCase(type) ? (val + " Night(s) tariff") : "FIXED_AMOUNT".equalsIgnoreCase(type) ? ("₹" + val) : (val + "%");

                    if ("AFTER_RESERVATION".equalsIgnoreCase(ruleType)) {
                        return "If cancelled immediately after reservation, " + chargeUnitStr + " penalty applies.";
                    }

                    if (val >= 100 && "PERCENTAGE".equalsIgnoreCase(type) && unit == 1 && "Day".equalsIgnoreCase(beforeUnit)) {
                        return "Flexible (100% refund up to 24h before check-in)";
                    }
                    if (val >= 100 && "PERCENTAGE".equalsIgnoreCase(type) && unit == 7 && "Day".equalsIgnoreCase(beforeUnit)) {
                        return "Moderate (100% refund up to 7 days before check-in)";
                    }
                    if (val >= 100 && "PERCENTAGE".equalsIgnoreCase(type) && unit == 14 && "Day".equalsIgnoreCase(beforeUnit)) {
                        return "Strict (100% refund up to 14 days before check-in)";
                    }
                    return "If cancelled within " + unit + " " + beforeUnit + "(s) of check-in, " + chargeUnitStr + " penalty applies.";
                }
                return "Custom Policy (" + activeRules.size() + " active refund tier(s) configured in Settings ➔ Policies)";
            }
        } catch (Exception ignored) {}
        return "Cancellation policy not configured";
    }

    private PropertyPolicyDTO mapToDTO(PropertyPolicy entity) {
        return new PropertyPolicyDTO(
                entity.getNoCancellationPolicy(),
                entity.getCancellationRulesJson(),
                entity.getTermsAndConditions(),
                entity.getPrivacyPolicy(),
                entity.getRefundPolicy(),
                entity.getChildAgePolicyJson()
        );
    }
}
