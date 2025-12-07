package com.hairstudio.api.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "OrderStatus", schema = "dbo", uniqueConstraints = {
        @UniqueConstraint(name = "UQ__OrderSta__737584F6AF6AB3E0", columnNames = {"Name"})
})
public class OrderStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderStatusId", nullable = false)
    private Short orderStatusId;

    @Nationalized
    @Column(name = "Name", nullable = false, length = 50)
    private String name;

    public Short getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(Short orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}