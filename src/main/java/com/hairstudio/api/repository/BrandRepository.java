package com.hairstudio.api.repository;

import com.hairstudio.api.dto.brands.BrandWithCheck;
import com.hairstudio.api.model.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Short> {

    List<Brand> findByIsActiveTrue();

    Page<Brand> findByIsActiveTrue(Pageable pageable);

    @Query("""
        SELECT new com.hairstudio.api.dto.brands.BrandWithCheck(
            b,
            (SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END
             FROM Product p
             WHERE p.brand = b AND p.isActive = TRUE)
        )
        FROM Brand b
        WHERE b.brandId = :brandId AND b.isActive = TRUE
    """)
    Optional<BrandWithCheck> getBrandWithCheck(Short brandId);

    boolean existsByName(String name);
}