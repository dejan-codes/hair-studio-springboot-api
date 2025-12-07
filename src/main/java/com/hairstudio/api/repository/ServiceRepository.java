package com.hairstudio.api.repository;

import com.hairstudio.api.model.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Short> {

    Page<Service> findByIsActiveTrue(Pageable pageable);

    List<Service> findByIsActiveTrue();

    List<Service> findByIsActiveTrueOrderBySequenceNumberAsc();

    long countByIsActiveTrue();
}