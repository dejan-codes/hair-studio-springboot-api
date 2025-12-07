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
@Table(name = "PaymentStatus", schema = "dbo", uniqueConstraints = {
        @UniqueConstraint(name = "UQ__PaymentS__737584F6BF54A398", columnNames = {"Name"})
})
public class PaymentStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PaymentStatusId", nullable = false)
    private Short paymentStatusId;

    @Nationalized
    @Column(name = "Name", nullable = false, length = 50)
    private String name;

    public Short getPaymentStatusId() {
        return paymentStatusId;
    }

    public void setPaymentStatusId(Short paymentStatusId) {
        this.paymentStatusId = paymentStatusId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}