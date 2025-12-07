package com.hairstudio.api.repository;

import com.hairstudio.api.model.entity.EmailConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailConfirmationRepository extends JpaRepository<EmailConfirmation, Short> {

    Optional<EmailConfirmation> findByConfirmationCode(String code);

    Optional<EmailConfirmation> findByUserUserId(Short userId);

}