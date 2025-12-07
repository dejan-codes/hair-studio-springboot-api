package com.hairstudio.api.dto.services;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

@Data
public class ServiceCreateDTO {
    @NotBlank
    @Size(min = 2, max = 50, message = "Service name must be between 2 and 50 characters.")
    private String name;

    @Size(max = 2000, message = "Description can't be longer than 2000 characters.")
    private String description;

    @NotNull
    @DecimalMin(value = "0.01", message = "Price must be greater than 0.")
    private BigDecimal price;

    @NotNull
    @DecimalMin(value = "0.0", message = "Discount must be positive.")
    private BigDecimal discount;

    @NotNull
    @Min(1)
    @Max(480)
    private Integer durationMinutes;

    @NotNull
    @Min(1)
    @Max(2)
    private Short genderId;

    @NotNull
    private MultipartFile image;

    @NotNull
    @Min(0)
    @Max(255)
    private Short sequenceNumber;
}
