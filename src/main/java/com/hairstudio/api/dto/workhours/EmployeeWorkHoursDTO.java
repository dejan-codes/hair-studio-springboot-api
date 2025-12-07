package com.hairstudio.api.dto.workhours;

import java.time.LocalDate;
import java.time.LocalTime;

public record EmployeeWorkHoursDTO(LocalDate date, LocalTime timeFrom, LocalTime timeTo) {}