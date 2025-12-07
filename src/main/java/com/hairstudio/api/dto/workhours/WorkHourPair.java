package com.hairstudio.api.dto.workhours;

import java.time.LocalDate;

public record WorkHourPair(Short userId, LocalDate date) {}