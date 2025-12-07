package com.hairstudio.api.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UserUpdateDTO {
    @NotBlank
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters.")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters.")
    private String lastName;

    @Size(max = 50, message = "Phone number can't be longer than 50 characters.")
    private String phoneNumber;

    @Size(max = 50, message = "Bio can't be longer than 50 characters.")
    private String bio;

    @NotBlank
    @Size(max = 100, message = "Email can't be longer than 100 characters.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotNull
    @Size(min = 1, message = "At least one role is required.")
    private List<Short> roles;

    @NotNull
    private MultipartFile image;
}