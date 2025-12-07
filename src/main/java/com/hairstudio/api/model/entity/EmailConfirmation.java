package com.hairstudio.api.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "EmailConfirmation", schema = "dbo")
public class EmailConfirmation {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "EmailConfirmationId", nullable = false)
    private UUID emailConfirmationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserId", nullable = false)
    private User user;

    @Nationalized
    @Column(name = "ConfirmationCode", nullable = false)
    private String confirmationCode;

    @Column(name = "ExpiresAt", nullable = false)
    private Instant expiresAt;

    public UUID getEmailConfirmationId() {
        return emailConfirmationId;
    }

    public void setEmailConfirmationId(UUID emailConfirmationId) {
        this.emailConfirmationId = emailConfirmationId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

}