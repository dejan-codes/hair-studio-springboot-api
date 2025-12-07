package com.hairstudio.api.service;

import com.hairstudio.api.common.ResultWithValue;
import com.hairstudio.api.common.ResultWithoutValue;
import com.hairstudio.api.dto.workhours.EmployeeWorkHoursDTO;
import com.hairstudio.api.dto.workhours.WorkHourDTO;
import com.hairstudio.api.dto.workhours.WorkHourDeleteDTO;
import com.hairstudio.api.dto.workhours.WorkHourForMonthDTO;

import java.time.LocalDate;
import java.util.List;

public interface WorkHourService {
    ResultWithoutValue createWorkHours(List<WorkHourDTO> dtoList);
    ResultWithValue<List<WorkHourForMonthDTO>> getWorkHours(LocalDate date);
    ResultWithValue<List<EmployeeWorkHoursDTO>> getEmployeeWorkHours(Short employeeId, LocalDate dateFrom, LocalDate dateTo);
    ResultWithoutValue deleteWorkHours(List<WorkHourDeleteDTO> dtoList);
}
