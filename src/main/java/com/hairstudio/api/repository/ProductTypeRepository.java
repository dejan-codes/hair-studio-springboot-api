package com.hairstudio.api.repository;

import com.hairstudio.api.dto.producttypes.ProductTypeWithCheck;
import com.hairstudio.api.model.entity.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, Short> {

    List<ProductType> findByIsActiveTrue();

    Page<ProductType> findByIsActiveTrue(Pageable pageable);

    @Query("""
        SELECT new com.hairstudio.api.dto.producttypes.ProductTypeWithCheck(b,
            (SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
             FROM Product p WHERE p.productType = b AND p.isActive = true))
        FROM ProductType b
        WHERE b.productTypeId = :productTypeId AND b.isActive = true
    """)
    Optional<ProductTypeWithCheck> getProductTypeWithCheck(Short productTypeId);

    boolean existsByName(String name);
}