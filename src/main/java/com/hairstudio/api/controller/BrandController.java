package com.hairstudio.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hairstudio.api.dto.brands.BrandCreateDTO;
import com.hairstudio.api.dto.brands.BrandUpdateDTO;
import com.hairstudio.api.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/Brand")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<?> getPagedBrands(@RequestParam int page, @RequestParam int rowsPerPage) {
        var result = brandService.getPagedBrands(page, rowsPerPage);
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @PostMapping
    public ResponseEntity<?> createBrand(@Valid @RequestBody BrandCreateDTO dto) {
        var result = brandService.createBrand(dto);
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @PutMapping("/{brandId}")
    public ResponseEntity<?> updateBrand(@PathVariable Short brandId,
                                         @Valid @RequestBody BrandUpdateDTO dto) {
        var result = brandService.updateBrand(brandId, dto);
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @DeleteMapping("/{brandId}")
    public ResponseEntity<?> deleteBrand(@PathVariable Short brandId) {
        var result = brandService.deleteBrand(brandId);
        return result.toResponseEntity();
    }

    @GetMapping("/brands-dropdown")
    public ResponseEntity<?> getBrandsForDropdown() {
        var result = brandService.getBrandsForDropdown();
        return result.toResponseEntity();
    }
}
