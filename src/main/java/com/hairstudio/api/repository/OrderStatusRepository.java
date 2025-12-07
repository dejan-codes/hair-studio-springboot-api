package com.hairstudio.api.repository;

import com.hairstudio.api.model.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Short> {

    Optional<OrderStatus> findByOrderStatusId(Short orderStatusId);
}