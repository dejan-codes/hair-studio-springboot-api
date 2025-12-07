package com.hairstudio.api.dto.services;

import java.math.BigDecimal;

public record ServiceSummaryDTO(
        short serviceId,
        String name,
        String description,
        BigDecimal price,
        String gender,
        byte[] image
) {}