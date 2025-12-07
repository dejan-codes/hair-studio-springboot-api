package com.hairstudio.api.dto.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserRegistrationDTO {
    @NotBlank
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters.")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters.")
    private String lastName;

    @NotBlank
    @Size(max = 100, message = "Email can't be longer than 100 characters.")
    @Email(message = "Invalid email format.")
    private String email;

    @Size(max = 50, message = "Phone number can't be longer than 50 characters.")
    private String phoneNumber;

    @NotBlank
    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters.")
    private String password;
}