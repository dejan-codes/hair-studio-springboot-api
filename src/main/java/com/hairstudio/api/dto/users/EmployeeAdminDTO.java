package com.hairstudio.api.dto.users;

import java.util.List;

public record EmployeeAdminDTO(
        short userId,
        String firstName,
        String lastName,
        String phoneNumber,
        String bio,
        String email,
        byte[] image,
        List<String> roles
) {}