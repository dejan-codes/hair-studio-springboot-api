package com.hairstudio.api.dto.products;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BuyProductDTO {
    @NotNull
    private Short productId;

    @NotNull
    @Min(0)
    @Max(255)
    private Short quantity;

    @NotNull
    @DecimalMin(value = "0.01", message = "Price must be greater than 0.")
    private BigDecimal price;
}