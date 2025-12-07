package com.hairstudio.api.dto.workhours;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class WorkHourDTO {

    @NotNull
    private Short employeeId;

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime timeFrom;

    @NotNull
    private LocalTime timeTo;

    public boolean isValid() {
        return timeFrom != null && timeTo != null && timeFrom.isBefore(timeTo);
    }
}