package com.hairstudio.api.dto.reservations;

import java.time.LocalDateTime;

public record ReservationDetailsDTO(
        String serviceName,
        String clientFullName,
        LocalDateTime start,
        LocalDateTime end,
        String phoneNumber,
        String email,
        String note
) {}