package com.hairstudio.api.dto.products;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
public class ProductCreateDTO {
    @NotBlank
    @Size(min = 2, max = 255, message = "Product name must be between 2 and 255 characters.")
    private String name;

    @Size(max = 1000, message = "Description can't be longer than 1000 characters.")
    private String description;

    @NotNull
    @DecimalMin(value = "0.01", message = "Price must be greater than 0.")
    private BigDecimal price;

    @NotNull
    @Min(0)
    private int stock;

    @NotNull
    @Min(0)
    @Max(255)
    private Short sequenceNumber;

    @NotNull
    private Short brandId;

    @NotNull
    private Short productTypeId;

    @NotNull
    private MultipartFile image;
}