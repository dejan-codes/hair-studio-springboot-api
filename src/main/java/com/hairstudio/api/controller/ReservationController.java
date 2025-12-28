package com.hairstudio.api.controller;

import com.hairstudio.api.common.Result;
import com.hairstudio.api.dto.reservations.EmployeeReservationCreateDTO;
import com.hairstudio.api.dto.reservations.UserReservationCreateDTO;
import com.hairstudio.api.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/Reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).USER.getRoleName()) or " +
            "hasRole(T(com.hairstudio.api.model.enums.RoleEnum).EMPLOYEE.getRoleName()) or " +
            "hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @PostMapping("/create-reservation")
    public ResponseEntity<?> createUserReservation(@RequestBody UserReservationCreateDTO dto) {
        Result result = reservationService.createUserReservation(dto);
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).USER.getRoleName()) or " +
            "hasRole(T(com.hairstudio.api.model.enums.RoleEnum).EMPLOYEE.getRoleName()) or " +
            "hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @PostMapping("/create-employee-reservation")
    public ResponseEntity<?> createEmployeeReservation(@RequestBody EmployeeReservationCreateDTO dto) {
        Result result = reservationService.createEmployeeReservation(dto);
        return result.toResponseEntity();
    }

    @GetMapping("/reservations")
    public ResponseEntity<?> getEmployeeReservations(
            @RequestParam int employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo
    ) {
        Result result = reservationService.getEmployeeReservations(employeeId, dateFrom, dateTo);
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).USER.getRoleName()) or " +
            "hasRole(T(com.hairstudio.api.model.enums.RoleEnum).EMPLOYEE.getRoleName()) or " +
            "hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @GetMapping("/reservation-details/{reservationId}")
    public ResponseEntity<?> getReservationDetails(@PathVariable short reservationId) {
        Result result = reservationService.getReservationDetails(reservationId);
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).USER.getRoleName()) or " +
            "hasRole(T(com.hairstudio.api.model.enums.RoleEnum).EMPLOYEE.getRoleName()) or " +
            "hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @PutMapping("/{reservationId}")
    public ResponseEntity<?> cancelReservation(@PathVariable short reservationId) {
        Result result = reservationService.cancelReservation(reservationId);
        return result.toResponseEntity();
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportCalendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
    ) throws IOException {
        if (from.isAfter(to))
            return ResponseEntity.badRequest().build();
        byte[] content = reservationService.exportCalendar(from, to);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=Reservations_Report.xlsx")
                .body(content);
    }
}
