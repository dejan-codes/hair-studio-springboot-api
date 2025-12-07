package com.hairstudio.api.dto.orders;

import java.math.BigDecimal;

public record OrderItemDetailDTO(String productName, BigDecimal price, int quantity) {}