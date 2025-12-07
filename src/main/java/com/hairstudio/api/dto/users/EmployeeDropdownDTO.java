package com.hairstudio.api.dto.users;

public record EmployeeDropdownDTO(
        short employeeId,
        String firstName,
        String lastName
) {}