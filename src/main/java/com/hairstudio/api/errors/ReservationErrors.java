package com.hairstudio.api.errors;

import com.hairstudio.api.common.Error;

public final class ReservationErrors {
    public static final Error RESERVATION_NOT_FOUND = new Error(
            "Reservation.ReservationNotFound", "Reservation not found.");
}