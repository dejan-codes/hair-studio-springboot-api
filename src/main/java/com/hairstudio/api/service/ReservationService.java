package com.hairstudio.api.service;

import com.hairstudio.api.common.ResultWithValue;
import com.hairstudio.api.common.ResultWithoutValue;
import com.hairstudio.api.dto.reservations.EmployeeReservationCreateDTO;
import com.hairstudio.api.dto.reservations.ReservationDetailsDTO;
import com.hairstudio.api.dto.reservations.ReservationSummaryDTO;
import com.hairstudio.api.dto.reservations.UserReservationCreateDTO;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface ReservationService {

    ResultWithoutValue createUserReservation(Short tokenUserId, UserReservationCreateDTO dto);

    ResultWithoutValue createEmployeeReservation(Short tokenUserId, EmployeeReservationCreateDTO dto);

    ResultWithValue<List<ReservationSummaryDTO>> getEmployeeReservations(
            Short tokenUserId,
            int employeeId,
            LocalDate from,
            LocalDate to
    );

    ResultWithValue<ReservationDetailsDTO> getReservationDetails(Short tokenUserId, short reservationId);

    ResultWithoutValue cancelReservation(Short tokenUserId, short reservationId);

    byte[] exportCalendar(Instant from, Instant to) throws IOException;
}