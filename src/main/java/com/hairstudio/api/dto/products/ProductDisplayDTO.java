package com.hairstudio.api.dto.products;

import java.math.BigDecimal;

public record ProductDisplayDTO(
        short productId,
        String name,
        String description,
        BigDecimal price,
        int stock,
        byte[] image,
        Short brandId,
        Short productTypeId,
        String brand,
        String productType,
        Short sequenceNumber
) {}