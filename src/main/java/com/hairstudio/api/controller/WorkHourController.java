package com.hairstudio.api.controller;

import com.hairstudio.api.dto.workhours.WorkHourDTO;
import com.hairstudio.api.dto.workhours.WorkHourDeleteDTO;
import com.hairstudio.api.service.WorkHourService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/WorkHour")
@RequiredArgsConstructor
public class WorkHourController {

    private final WorkHourService workHourService;

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @PostMapping
    public ResponseEntity<?> createWorkHours(@RequestBody List<WorkHourDTO> dtoList) {
        var result = workHourService.createWorkHours(dtoList);
        return result.toResponseEntity();
    }

    @GetMapping
    public ResponseEntity<?> getWorkHours(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var result = workHourService.getWorkHours(date);
        return result.toResponseEntity();
    }

    @GetMapping("/employee")
    public ResponseEntity<?> getEmployeeWorkHours(
            @RequestParam Short employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        var result = workHourService.getEmployeeWorkHours(employeeId, dateFrom, dateTo);
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @DeleteMapping
    public ResponseEntity<?> deleteWorkHours(@RequestBody List<WorkHourDeleteDTO> dtoList) {
        var result = workHourService.deleteWorkHours(dtoList);
        return result.toResponseEntity();
    }
}
