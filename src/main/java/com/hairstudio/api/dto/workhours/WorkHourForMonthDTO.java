package com.hairstudio.api.dto.workhours;

import java.time.LocalTime;

public record WorkHourForMonthDTO(
        short workHourId,
        short employeeId,
        int day,
        LocalTime timeFrom,
        LocalTime timeTo
) {}