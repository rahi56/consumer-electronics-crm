
package com.rahim.crm.customer.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RegisteredProductResponse {

    private Long id;
    private String serialNumber;
    private String productName;
    private String model;
    private LocalDate purchaseDate;
    private LocalDate warrantyExpiryDate;
    private String warrantyStatus;
    private LocalDateTime createdAt;

    public RegisteredProductResponse() {
    }

    public RegisteredProductResponse(
            Long id,
            String serialNumber,
            String productName,
            String model,
            LocalDate purchaseDate,
            LocalDate warrantyExpiryDate,
            String warrantyStatus,
            LocalDateTime createdAt) {

        this.id = id;
        this.serialNumber = serialNumber;
        this.productName = productName;
        this.model = model;
        this.purchaseDate = purchaseDate;
        this.warrantyExpiryDate = warrantyExpiryDate;
        this.warrantyStatus = warrantyStatus;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getProductName() {
        return productName;
    }

    public String getModel() {
        return model;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public LocalDate getWarrantyExpiryDate() {
        return warrantyExpiryDate;
    }

    public String getWarrantyStatus() {
        return warrantyStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
