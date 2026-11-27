package stayops_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stayops_api.dto.TaxSettingsDTO;
import stayops_api.entity.Property;
import stayops_api.entity.TaxSettings;
import stayops_api.repository.PropertyRepository;
import stayops_api.repository.TaxSettingsRepository;

@Service
public class TaxSettingsService {

    private final TaxSettingsRepository taxSettingsRepository;
    private final PropertyRepository propertyRepository;

    public TaxSettingsService(TaxSettingsRepository taxSettingsRepository, PropertyRepository propertyRepository) {
        this.taxSettingsRepository = taxSettingsRepository;
        this.propertyRepository = propertyRepository;
    }

    @Transactional
    public TaxSettingsDTO getTaxSettingsByProperty(Long propertyId, Long userId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        TaxSettings settings = taxSettingsRepository.findByPropertyId(propertyId)
                .orElseGet(() -> {
                    TaxSettings newSettings = new TaxSettings(property);
                    return taxSettingsRepository.save(newSettings);
                });

        return mapToDTO(settings);
    }

    @Transactional
    public TaxSettingsDTO saveTaxSettingsByProperty(Long propertyId, Long userId, TaxSettingsDTO dto) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        TaxSettings settings = taxSettingsRepository.findByPropertyId(propertyId)
                .orElseGet(() -> new TaxSettings(property));

        if (dto.getIsGstRegistered() != null) settings.setIsGstRegistered(dto.getIsGstRegistered());
        if (dto.getGstin() != null) settings.setGstin(dto.getGstin().trim().toUpperCase());
        if (dto.getTaxesEnabled() != null) settings.setTaxesEnabled(dto.getTaxesEnabled());
        if (dto.getTaxMode() != null) settings.setTaxMode(dto.getTaxMode());
        if (dto.getFixedTaxRate() != null) settings.setFixedTaxRate(dto.getFixedTaxRate());
        if (dto.getTaxInclusive() != null) settings.setTaxInclusive(dto.getTaxInclusive());
        if (dto.getServiceChargeEnabled() != null) settings.setServiceChargeEnabled(dto.getServiceChargeEnabled());
        if (dto.getServiceChargePercent() != null) settings.setServiceChargePercent(dto.getServiceChargePercent());
        if (dto.getSpecifiedPremises() != null) settings.setSpecifiedPremises(dto.getSpecifiedPremises());

        TaxSettings saved = taxSettingsRepository.save(settings);
        return mapToDTO(saved);
    }

    private TaxSettingsDTO mapToDTO(TaxSettings entity) {
        return new TaxSettingsDTO(
                entity.getIsGstRegistered(),
                entity.getGstin(),
                entity.getTaxesEnabled(),
                entity.getTaxMode(),
                entity.getFixedTaxRate(),
                entity.getTaxInclusive(),
                entity.getServiceChargeEnabled(),
                entity.getServiceChargePercent(),
                entity.getSpecifiedPremises()
        );
    }
}
