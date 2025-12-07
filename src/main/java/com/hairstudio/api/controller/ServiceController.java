package com.hairstudio.api.controller;

import com.hairstudio.api.common.ResultWithoutValue;
import com.hairstudio.api.dto.services.ServiceCreateDTO;
import com.hairstudio.api.dto.services.ServiceUpdateDTO;
import com.hairstudio.api.errors.UserErrors;
import com.hairstudio.api.security.CurrentUserContext;
import com.hairstudio.api.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/Service")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;
    private final CurrentUserContext currentUserContext;

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createService(@ModelAttribute ServiceCreateDTO dto) {
        var userId = currentUserContext.getUserId();
        if (userId == null) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND).toResponseEntity();
        }

        var result = serviceService.createService(dto, userId);
        return result.toResponseEntity();
    }

    @GetMapping("/service-dropdown")
    public ResponseEntity<?> getServicesForDropdown() {
        var userId = currentUserContext.getUserId();
        if (userId == null) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND).toResponseEntity();
        }

        var result = serviceService.getServicesForDropdown(userId);
        return result.toResponseEntity();
    }

    @GetMapping("/all-services")
    public ResponseEntity<?> getAllServices(@RequestParam int page, @RequestParam int rowsPerPage) {
        var result = serviceService.getAllServices(page, rowsPerPage);
        return result.toResponseEntity();
    }

    @GetMapping("/service-list")
    public ResponseEntity<?> getServicesByGender() {
        var result = serviceService.getServicesByGender();
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @PutMapping(path = "/{serviceId}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateService(@PathVariable Short serviceId, @ModelAttribute ServiceUpdateDTO dto) {
        var userId = currentUserContext.getUserId();
        if (userId == null) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND).toResponseEntity();
        }

        var result = serviceService.updateService(serviceId, dto, userId);
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @DeleteMapping("/{serviceId}")
    public ResponseEntity<?> deleteService(@PathVariable Short serviceId) {
        var userId = currentUserContext.getUserId();
        if (userId == null) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND).toResponseEntity();
        }

        var result = serviceService.deleteService(serviceId, userId);
        return result.toResponseEntity();
    }
}
