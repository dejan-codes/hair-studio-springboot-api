package com.hairstudio.api.controller;

import com.hairstudio.api.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/Message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @GetMapping
    public ResponseEntity<?> getBrandsForDropdown(@RequestParam int page, @RequestParam int rowsPerPage) {
        var result = messageService.getMessages(page, rowsPerPage);
        return result.toResponseEntity();
    }
}
