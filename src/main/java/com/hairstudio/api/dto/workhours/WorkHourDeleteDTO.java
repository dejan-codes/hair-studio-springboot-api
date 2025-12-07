package com.hairstudio.api.dto.workhours;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkHourDeleteDTO {
    @NotNull
    private Short employeeId;

    @NotNull
    private LocalDate date;
}