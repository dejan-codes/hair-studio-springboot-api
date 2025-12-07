package com.hairstudio.api.controller;

import com.hairstudio.api.common.ResultWithoutValue;
import com.hairstudio.api.errors.UserErrors;
import com.hairstudio.api.security.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hairstudio.api.dto.producttypes.ProductTypeCreateDTO;
import com.hairstudio.api.dto.producttypes.ProductTypeUpdateDTO;
import com.hairstudio.api.service.ProductTypeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/ProductType")
@RequiredArgsConstructor
public class ProductTypeController {

    private final ProductTypeService productTypeService;
    private final CurrentUserContext currentUserContext;

    @GetMapping
    public ResponseEntity<?> getPagedProductTypes(@RequestParam int page, @RequestParam int rowsPerPage) {
        var result = productTypeService.getPagedProductTypes(page, rowsPerPage);
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @PostMapping
    public ResponseEntity<?> createProductType(@Valid @RequestBody ProductTypeCreateDTO dto) {
        var userId = currentUserContext.getUserId();
        if (userId == null) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND).toResponseEntity();
        }
        var result = productTypeService.createProductType(dto, userId);
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @PutMapping("/{productTypeId}")
    public ResponseEntity<?> updateProductType(@PathVariable Short productTypeId,
                                         @Valid @RequestBody ProductTypeUpdateDTO dto) {
        var userId = currentUserContext.getUserId();
        if (userId == null) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND).toResponseEntity();
        }
        var result = productTypeService.updateProductType(productTypeId, dto, userId);
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @DeleteMapping("/{productTypeId}")
    public ResponseEntity<?> deleteProductType(@PathVariable Short productTypeId) {
        var userId = currentUserContext.getUserId();
        if (userId == null) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND).toResponseEntity();
        }
        var result = productTypeService.deleteProductType(productTypeId, userId);
        return result.toResponseEntity();
    }

    @GetMapping("/types-dropdown")
    public ResponseEntity<?> getProductTypesForDropdown() {
        var result = productTypeService.getProductTypesForDropdown();
        return result.toResponseEntity();
    }
}
