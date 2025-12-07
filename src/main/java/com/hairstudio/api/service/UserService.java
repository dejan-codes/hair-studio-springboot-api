package com.hairstudio.api.service;

import com.hairstudio.api.common.ResultWithValue;
import com.hairstudio.api.common.ResultWithoutValue;
import com.hairstudio.api.dto.users.UserRegistrationDTO;
import com.hairstudio.api.dto.users.TokenDTO;
import com.hairstudio.api.dto.users.LoginDTO;
import com.hairstudio.api.dto.users.UserCreateDTO;
import com.hairstudio.api.dto.users.EmployeeDropdownDTO;
import com.hairstudio.api.dto.users.PagedUsersDTO;
import com.hairstudio.api.dto.users.EmployeeDetailsDTO;
import com.hairstudio.api.dto.users.UserUpdateDTO;
import com.hairstudio.api.dto.users.PasswordUpdateDTO;

import java.util.List;

public interface UserService {
    ResultWithoutValue registerUser(UserRegistrationDTO dto);
    ResultWithoutValue confirmUserEmail(String code);
    ResultWithoutValue userPasswordReset(String email);
    ResultWithValue<TokenDTO> login(LoginDTO loginDTO);
    ResultWithoutValue resendConfirmationCode(String email);
    ResultWithoutValue createUser(UserCreateDTO dto, Short tokenUserId);
    ResultWithoutValue deleteUser(Short userId, Short tokenUserId);
    ResultWithValue<List<EmployeeDropdownDTO>> getEmployeeForDropdown();
    ResultWithValue<PagedUsersDTO> getUsersForAdmin(int page, int rowsPerPage, String search);
    ResultWithValue<List<EmployeeDetailsDTO>> getEmployees();
    ResultWithoutValue updateUser(Short userId, UserUpdateDTO dto, Short tokenUserId);
    ResultWithoutValue updatePassword(Short tokenUserId, PasswordUpdateDTO dto);
}