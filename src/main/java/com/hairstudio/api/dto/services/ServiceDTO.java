package com.hairstudio.api.dto.services;

import java.math.BigDecimal;

public record ServiceDTO(
        Short serviceId,
        String name,
        String description,
        BigDecimal price,
        BigDecimal discount,
        int durationMinutes,
        Short genderId,
        byte[] image,
        Short sequenceNumber
) {}