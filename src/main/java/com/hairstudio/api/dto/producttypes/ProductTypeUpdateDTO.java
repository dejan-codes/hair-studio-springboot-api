package com.hairstudio.api.dto.producttypes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductTypeUpdateDTO {
    @NotBlank
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters.")
    String name;
}