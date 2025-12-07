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
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ProductType", schema = "dbo", uniqueConstraints = {
        @UniqueConstraint(name = "UQ__ProductT__737584F69DC71BFF", columnNames = {"Name"})
})
public class ProductType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductTypeId", nullable = false)
    private Short productTypeId;

    @Nationalized
    @Column(name = "Name", nullable = false, length = 50)
    private String name;

    @ColumnDefault("0")
    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = false;

    public Short getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(Short productTypeId) {
        this.productTypeId = productTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

}