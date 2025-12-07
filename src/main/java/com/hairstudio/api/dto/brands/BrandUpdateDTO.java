package com.hairstudio.api.dto.brands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BrandUpdateDTO {
    @NotBlank
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters.")
    private String name;
}