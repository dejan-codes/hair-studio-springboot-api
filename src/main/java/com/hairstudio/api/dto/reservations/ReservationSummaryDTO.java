package com.hairstudio.api.dto.reservations;

import java.time.LocalDateTime;

public record ReservationSummaryDTO(
        short serviceId,
        short reservationId,
        String serviceName,
        String clientFullName,
        LocalDateTime start,
        LocalDateTime end,
        boolean showCancelButton
) {}