package com.hairstudio.api.dto.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmailConfirmationDTO {
    @NotBlank
    @Size(min = 2, max = 255, message = "Code must be between 2 and 255 characters.")
    private String code;
}