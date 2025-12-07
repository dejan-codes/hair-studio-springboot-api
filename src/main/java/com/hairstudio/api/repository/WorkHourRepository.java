package com.hairstudio.api.repository;

import com.hairstudio.api.model.entity.WorkHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkHourRepository extends JpaRepository<WorkHour, Short> {

    @Query("SELECT w FROM WorkHour w WHERE YEAR(w.date) = :year AND MONTH(w.date) = :month")
    List<WorkHour> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    List<WorkHour> findByUserUserIdAndDateBetween(Short userId, LocalDate startDate, LocalDate endDate);
}