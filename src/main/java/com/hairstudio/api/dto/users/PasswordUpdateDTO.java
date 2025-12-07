package com.hairstudio.api.dto.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordUpdateDTO {
    @NotBlank
    @Size(min = 6, max = 50, message = "Password must be between 6 and 50 characters.")
    private String oldPassword;

    @NotBlank
    @Size(min = 6, max = 50, message = "Password must be between 6 and 50 characters.")
    private String newPassword;
}