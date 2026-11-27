package stayops_api.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "petty_cash_transactions")
public class PettyCashTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "property_id", nullable = false)
    private Long propertyId = 1L;

    @Column(name = "property_name", length = 150)
    private String propertyName = "Property";

    @Column(nullable = false, length = 15)
    private String type; // CREDIT or DEBIT

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "receipt_image_url", columnDefinition = "LONGTEXT")
    private String receiptImageUrl;

    @Column(name = "added_by", length = 100)
    private String addedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public PettyCashTransaction() {}

    public PettyCashTransaction(Long propertyId, String propertyName, String type, BigDecimal amount, String category, String notes, String receiptImageUrl, String addedBy) {
        this.propertyId = propertyId;
        this.propertyName = propertyName;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.notes = notes;
        this.receiptImageUrl = receiptImageUrl;
        this.addedBy = addedBy;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

    public String getPropertyName() { return propertyName; }
    public void setPropertyName(String propertyName) { this.propertyName = propertyName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getReceiptImageUrl() { return receiptImageUrl; }
    public void setReceiptImageUrl(String receiptImageUrl) { this.receiptImageUrl = receiptImageUrl; }

    public String getAddedBy() { return addedBy; }
    public void setAddedBy(String addedBy) { this.addedBy = addedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
