package com.hairstudio.api.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResendConfirmationCodeDTO {
    @NotBlank
    @Size(max = 100, message = "Email can't be longer than 100 characters.")
    @Email(message = "Invalid email format.")
    private String email;
}