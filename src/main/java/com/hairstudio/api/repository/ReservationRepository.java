package com.hairstudio.api.repository;

import com.hairstudio.api.model.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Short> {

    @Query("SELECT r FROM Reservation r " +
            "LEFT JOIN FETCH r.service " +
            "LEFT JOIN FETCH r.clientUser " +
            "LEFT JOIN FETCH r.clientCustomer " +
            "LEFT JOIN FETCH r.employee " +
            "WHERE r.reservationId = :reservationId")
    Optional<Reservation> getReservationWithDetails(@Param("reservationId") Short reservationId);

    @Query("SELECT r FROM Reservation r " +
            "JOIN r.employee e " +
            "WHERE e.userId = :employeeId " +
            "AND r.dateFrom >= :from AND r.dateTo <= :to " +
            "AND r.isActive = true")
    List<Reservation> getReservationsByEmployeeAndDate(@Param("employeeId") int employeeId,
                                                       @Param("from") Instant from,
                                                       @Param("to") Instant to);
}