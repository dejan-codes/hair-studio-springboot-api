package com.hairstudio.api.errors;

import com.hairstudio.api.common.Error;

public final class WorkHourErrors {
    public static final Error TIME_RANGE_ERROR = new Error(
            "WorkHour.TimeRangeError", "Time from cannot be greater than time to.");
}