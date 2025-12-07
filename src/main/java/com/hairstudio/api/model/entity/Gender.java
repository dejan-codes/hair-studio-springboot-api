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
@Table(name = "Gender", schema = "dbo", uniqueConstraints = {
        @UniqueConstraint(name = "UQ__Gender__737584F6EEF95A30", columnNames = {"Name"})
})
public class Gender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GenderId", columnDefinition = "tinyint not null")
    private Short genderId;

    @Nationalized
    @Column(name = "Name", nullable = false, length = 6)
    private String name;

    public Short getGenderId() {
        return genderId;
    }

    public void setGenderId(Short genderId) {
        this.genderId = genderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}