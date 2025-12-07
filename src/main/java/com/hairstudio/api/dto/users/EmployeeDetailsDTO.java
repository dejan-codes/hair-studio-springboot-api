package com.hairstudio.api.dto.users;

public record EmployeeDetailsDTO(
        String name,
        String bio,
        String email,
        byte[] image
) {}