package com.hairstudio.api.controller;

import com.hairstudio.api.common.ResultWithoutValue;
import com.hairstudio.api.dto.users.EmailConfirmationDTO;
import com.hairstudio.api.dto.users.LoginDTO;
import com.hairstudio.api.dto.users.PasswordResetDTO;
import com.hairstudio.api.dto.users.ResendConfirmationCodeDTO;
import com.hairstudio.api.dto.users.UserCreateDTO;
import com.hairstudio.api.dto.users.UserRegistrationDTO;
import com.hairstudio.api.dto.users.UserUpdateDTO;
import com.hairstudio.api.dto.users.PasswordUpdateDTO;
import com.hairstudio.api.errors.UserErrors;
import com.hairstudio.api.security.CurrentUserContext;
import com.hairstudio.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/User")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CurrentUserContext currentUserContext;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDTO dto) {
        var result = userService.registerUser(dto);
        return result.toResponseEntity();
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<?> confirmEmail(@Valid @RequestBody EmailConfirmationDTO dto) {
        var result = userService.confirmUserEmail(dto.getCode());
        return result.toResponseEntity();
    }

    @PostMapping("/password-reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetDTO dto) {
        var result = userService.userPasswordReset(dto.getEmail());
        return result.toResponseEntity();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        var result = userService.login(loginDTO);
        return result.toResponseEntity();
    }

    @PostMapping("/resend-confirmation")
    public ResponseEntity<?> resendConfirmation(@Valid @RequestBody ResendConfirmationCodeDTO dto) {
        var result = userService.resendConfirmationCode(dto.getEmail());
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @ModelAttribute UserCreateDTO dto) {
        Short tokenUserId = currentUserContext.getUserId();
        if (tokenUserId == null) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND).toResponseEntity();
        }
        var result = userService.createUser(dto, tokenUserId);
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Short userId) {
        Short tokenUserId = currentUserContext.getUserId();
        if (tokenUserId == null) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND).toResponseEntity();
        }
        var result = userService.deleteUser(userId, tokenUserId);
        return result.toResponseEntity();
    }

    @GetMapping("/employee-dropdown")
    public ResponseEntity<?> getEmployeesForDropdown() {
        var result = userService.getEmployeeForDropdown();
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @GetMapping("/users-administration")
    public ResponseEntity<?> getUsersForAdmin(@RequestParam int page,
                                              @RequestParam int rowsPerPage,
                                              @RequestParam(required = false) String search) {
        var result = userService.getUsersForAdmin(page, rowsPerPage, search);
        return result.toResponseEntity();
    }

    @GetMapping("/employees")
    public ResponseEntity<?> getEmployees() {
        var result = userService.getEmployees();
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Short userId, @Valid @ModelAttribute UserUpdateDTO dto) {
        Short tokenUserId = currentUserContext.getUserId();
        if (tokenUserId == null) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND).toResponseEntity();
        }
        var result = userService.updateUser(userId, dto, tokenUserId);
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).USER.getRoleName()) or " +
            "hasRole(T(com.hairstudio.api.model.enums.RoleEnum).EMPLOYEE.getRoleName()) or " +
            "hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody PasswordUpdateDTO dto) {
        Short tokenUserId = currentUserContext.getUserId();
        if (tokenUserId == null) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND).toResponseEntity();
        }
        var result = userService.updatePassword(tokenUserId, dto);
        return result.toResponseEntity();
    }
}
