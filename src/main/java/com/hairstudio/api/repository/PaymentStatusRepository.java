package com.hairstudio.api.repository;

import com.hairstudio.api.model.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Short> {

    Optional<PaymentStatus> findByName(String name);
}