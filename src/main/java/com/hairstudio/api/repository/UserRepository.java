package com.hairstudio.api.repository;

import com.hairstudio.api.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Short> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndEmailConfirmedFalse(String email);

    List<User> findAllByIsActiveTrue();

    @Query("""
    SELECT DISTINCT u
    FROM User u
    JOIN u.userRoles ur
    JOIN ur.role r
    WHERE u.isActive = true
      AND r.name = :roleName
    """)
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role WHERE u.userId = :userId")
    Optional<User> findByUserIdWithRoles(Short userId);

    @Query("""
    SELECT DISTINCT u
    FROM User u
    JOIN u.userRoles ur
    JOIN ur.role r
    LEFT JOIN FETCH u.reservations res
    LEFT JOIN u.reservations resFilter
         WITH resFilter.dateFrom >= :from AND resFilter.dateTo <= :to
    WHERE r.name = :roleName
    """)
    List<User> getEmployeesWithReservations(
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("roleName") String roleName
    );
}