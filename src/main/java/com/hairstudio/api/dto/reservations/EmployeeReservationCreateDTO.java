package com.hairstudio.api.dto.reservations;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmployeeReservationCreateDTO {
    @NotNull
    private Short serviceId;

    @NotNull
    private Short employeeId;

    @NotNull
    private LocalDateTime dateFrom;

    @NotNull
    private LocalDateTime dateTo;

    @NotBlank
    @Size(max = 50, message = "Full name can't be longer than 50 characters.")
    private String fullName;

    @Pattern(regexp = "^\\+?[0-9]*$", message = "Invalid phone number")
    private String phone;

    @NotBlank
    @Size(max = 100, message = "Employees note can't be longer than 100 characters.")
    private String note;

    public boolean isValid() {
        return dateFrom != null && dateTo != null && dateFrom.isBefore(dateTo);
    }
}