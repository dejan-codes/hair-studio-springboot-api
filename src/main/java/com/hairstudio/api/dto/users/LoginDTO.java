package com.hairstudio.api.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank
    @Email(message = "Invalid email format.")
    @Size(max = 100, message = "Email can't be longer than 100 characters.")
    private String email;

    @NotBlank
    @Size(min = 6, max = 50, message = "Password must be between 6 and 50 characters.")
    private String password;
}