package com.hairstudio.api.dto.reservations;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserReservationCreateDTO {
    @NotNull
    private Short serviceId;

    @NotNull
    private Short employeeId;

    @NotNull
    private LocalDateTime dateFrom;

    @NotNull
    private LocalDateTime dateTo;

    @Size(max = 100, message = "Note can't be longer than 100 characters.")
    private String note;

    public boolean isValid() {
        return dateFrom != null && dateTo != null && dateFrom.isBefore(dateTo);
    }
}