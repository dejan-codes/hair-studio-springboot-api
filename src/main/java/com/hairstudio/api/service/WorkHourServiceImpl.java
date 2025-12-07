package com.hairstudio.api.service;

import com.hairstudio.api.audit.Auditable;
import com.hairstudio.api.common.ResultWithValue;
import com.hairstudio.api.common.ResultWithoutValue;
import com.hairstudio.api.dto.workhours.WorkHourDTO;
import com.hairstudio.api.dto.workhours.WorkHourForMonthDTO;
import com.hairstudio.api.dto.workhours.EmployeeWorkHoursDTO;
import com.hairstudio.api.dto.workhours.WorkHourDeleteDTO;
import com.hairstudio.api.dto.workhours.WorkHourPair;
import com.hairstudio.api.errors.WorkHourErrors;
import com.hairstudio.api.model.entity.User;
import com.hairstudio.api.model.entity.WorkHour;
import com.hairstudio.api.repository.UserRepository;
import com.hairstudio.api.repository.WorkHourRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WorkHourServiceImpl implements WorkHourService {

    private final WorkHourRepository workHourRepository;
    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    @Auditable(action = "CREATE_WORK_HOURS")
    public ResultWithoutValue createWorkHours(List<WorkHourDTO> dtoList) {
        if (dtoList.stream().anyMatch(o ->
                o.getTimeFrom() == null ||
                        o.getTimeTo() == null ||
                        !o.getTimeFrom().isBefore(o.getTimeTo())
        )){
            return ResultWithoutValue.failure(WorkHourErrors.TIME_RANGE_ERROR);
        }

        var employeeIds = dtoList.stream()
                .map(WorkHourDTO::getEmployeeId)
                .distinct()
                .toList();

        var users = userRepository.findAllById(employeeIds);

        var userMap = users.stream()
                .filter(User::getIsActive)
                .collect(Collectors.toMap(User::getUserId, u -> u));

        workHourRepository.saveAll(
                dtoList.stream()
                        .map(dto -> {
                            var user = userMap.get(dto.getEmployeeId());
                            WorkHour wh = new WorkHour();
                            wh.setUser(user);
                            wh.setDate(dto.getDate());
                            wh.setTimeFrom(dto.getTimeFrom());
                            wh.setTimeTo(dto.getTimeTo());
                            return wh;
                        })
                        .toList()
        );

        return ResultWithoutValue.success();
    }

    @Override
    public ResultWithValue<List<WorkHourForMonthDTO>> getWorkHours(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();

        var workHours = workHourRepository.findByYearAndMonth(year, month);

        List<WorkHourForMonthDTO> list = workHours.stream()
                .map(o -> new WorkHourForMonthDTO(
                        o.getWorkHourId(),
                        o.getUser().getUserId(),
                        o.getDate().getDayOfMonth(),
                        o.getTimeFrom(),
                        o.getTimeTo()
                )).toList();

        return ResultWithValue.success(list);
    }

    @Override
    public ResultWithValue<List<EmployeeWorkHoursDTO>> getEmployeeWorkHours(Short employeeId, LocalDate dateFrom, LocalDate dateTo) {
        var workHours = workHourRepository.findByUserUserIdAndDateBetween(employeeId, dateFrom, dateTo);

        List<EmployeeWorkHoursDTO> list = workHours.stream()
                .map(o -> new EmployeeWorkHoursDTO(
                        o.getDate(),
                        o.getTimeFrom(),
                        o.getTimeTo()
                )).toList();

        return ResultWithValue.success(list);
    }

    @Override
    @Transactional
    @Auditable(action = "DELETE_WORK_HOURS")
    public ResultWithoutValue deleteWorkHours(List<WorkHourDeleteDTO> dtoList) {
        List<WorkHourPair> pairs = dtoList.stream()
                .map(dto -> new WorkHourPair(dto.getEmployeeId(), dto.getDate()))
                .toList();

        deleteByUserAndDateIn(pairs);

        return ResultWithoutValue.success();
    }

    @Transactional
    private void deleteByUserAndDateIn(List<WorkHourPair> pairs) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaDelete<WorkHour> criteriaDelete = criteriaBuilder.createCriteriaDelete(WorkHour.class);
        Root<WorkHour> root = criteriaDelete.from(WorkHour.class);

        Predicate[] predicates = pairs.stream()
                .map(pair -> criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("user").get("userId"), pair.userId()),
                        criteriaBuilder.equal(root.get("date"), pair.date())
                ))
                .toArray(Predicate[]::new);

        criteriaDelete.where(criteriaBuilder.or(predicates));

        jakarta.persistence.Query query = entityManager.createQuery(criteriaDelete);
        query.executeUpdate();
    }
}