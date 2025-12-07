package com.hairstudio.api.service;

import com.hairstudio.api.audit.Auditable;
import com.hairstudio.api.common.ResultWithValue;
import com.hairstudio.api.common.ResultWithoutValue;
import com.hairstudio.api.dto.users.LoginDTO;
import com.hairstudio.api.dto.users.TokenDTO;
import com.hairstudio.api.dto.users.UserRegistrationDTO;
import com.hairstudio.api.dto.users.UserCreateDTO;
import com.hairstudio.api.dto.users.UserUpdateDTO;
import com.hairstudio.api.dto.users.PasswordUpdateDTO;
import com.hairstudio.api.dto.users.EmployeeDropdownDTO;
import com.hairstudio.api.dto.users.EmployeeAdminDTO;
import com.hairstudio.api.dto.users.EmployeeDetailsDTO;
import com.hairstudio.api.dto.users.PagedUsersDTO;
import com.hairstudio.api.model.enums.RoleEnum;
import com.hairstudio.api.errors.UserErrors;
import com.hairstudio.api.errors.ValidationErrors;
import com.hairstudio.api.model.entity.EmailConfirmation;
import com.hairstudio.api.model.entity.PasswordResetToken;
import com.hairstudio.api.model.entity.User;
import com.hairstudio.api.model.entity.Role;
import com.hairstudio.api.model.entity.UserRole;
import com.hairstudio.api.model.entity.UserRoleId;
import com.hairstudio.api.model.entity.Message;
import com.hairstudio.api.repository.UserRepository;
import com.hairstudio.api.repository.MessageRepository;
import com.hairstudio.api.repository.EmailConfirmationRepository;
import com.hairstudio.api.repository.RoleRepository;
import com.hairstudio.api.repository.PasswordResetTokenRepository;
import com.hairstudio.api.security.CustomUserDetails;
import com.hairstudio.api.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    @Value("${frontend.url}")
    private String frontendUrl;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final EmailConfirmationRepository emailConfirmationRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    @Auditable(action = "REGISTER_USER")
    public ResultWithoutValue registerUser(UserRegistrationDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            return ResultWithoutValue.failure(UserErrors.USER_EXISTS);
        }

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setEmailConfirmed(false);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        Optional<Role> roleUserOptional = roleRepository.findById(RoleEnum.USER.getId());
        if (roleUserOptional.isEmpty()) {
            return ResultWithoutValue.failure(UserErrors.USER_EXISTS);
        }
        Role roleUser = roleUserOptional.get();
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(roleUser);
        UserRoleId userRoleId = new UserRoleId(user.getUserId(), roleUser.getRoleId());
        userRole.setId(userRoleId);
        List<UserRole> userRoles = new ArrayList<>();
        userRoles.add(userRole);
        user.setUserRoles(userRoles);
        userRepository.save(user);

        byte[] tokenBytes = new byte[32];
        new Random().nextBytes(tokenBytes);
        String confirmationCode = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

        EmailConfirmation emailConfirmation = new EmailConfirmation();
        emailConfirmation.setEmailConfirmationId(UUID.randomUUID());
        emailConfirmation.setUser(user);
        emailConfirmation.setConfirmationCode(confirmationCode);
        emailConfirmation.setExpiresAt(Instant.now().plusSeconds(24 * 3600));

        emailConfirmationRepository.save(emailConfirmation);

        String link = frontendUrl + "/confirm-email?code=" + confirmationCode;
        emailService.sendEmail(user.getEmail(), "Confirm Your Email", link);

        return ResultWithoutValue.success();
    }

    @Override
    @Transactional
    @Auditable(action = "CONFIRM_USER_EMAIL")
    public ResultWithoutValue confirmUserEmail(String code) {
        Optional<EmailConfirmation> confirmationOpt = emailConfirmationRepository.findByConfirmationCode(code);
        if (confirmationOpt.isEmpty() || confirmationOpt.get().getExpiresAt().isBefore(Instant.now())) {
            return ResultWithoutValue.failure(UserErrors.INVALID_CONFIRMATION_CODE);
        }

        EmailConfirmation confirmation = confirmationOpt.get();
        User user = confirmation.getUser();
        user.setEmailConfirmed(true);
        user.setIsActive(true);

        userRepository.save(user);
        emailConfirmationRepository.delete(confirmation);

        return ResultWithoutValue.success();
    }

    @Override
    @Transactional
    @Auditable(action = "USER_PASSWORD_RESET")
    public ResultWithoutValue userPasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResultWithoutValue.failure(UserErrors.EMAIL_NOT_FOUND);
        }

        User user = userOpt.get();
        String resetToken = UUID.randomUUID().toString();

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setCreatedAt(Instant.now());
        token.setResetToken(resetToken);
        token.setExpiryDate(Instant.now().plusSeconds(120 * 60));

        passwordResetTokenRepository.save(token);
        emailService.sendEmail(user.getEmail(),
                "Password reset",
                "New password is: " + resetToken + ". After logging in, it is advisable to reset password.");

        return ResultWithoutValue.success();
    }

    @Override
    @Auditable(action = "LOGIN")
    public ResultWithValue<TokenDTO> login(LoginDTO loginDTO) {
        User user = userRepository.findByEmailWithRoles(loginDTO.getEmail())
                .orElse(null);
        if (user == null) {
            return ResultWithValue.failure(UserErrors.USER_NOT_FOUND);
        }

        if (!user.getEmailConfirmed()) {
            return ResultWithValue.failure(UserErrors.EMAIL_CONFIRMATION_NOT_FOUND);
        }

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPasswordHash())) {
            Optional<PasswordResetToken> resetTokenOpt = user.getPasswordResetTokens().stream()
                    .filter(t -> t.getExpiryDate().isAfter(Instant.now()))
                    .findFirst();

            if (resetTokenOpt.isEmpty() || !resetTokenOpt.get().getResetToken().equals(loginDTO.getPassword())) {
                return ResultWithValue.failure(UserErrors.INCORRECT_PASSWORD);
            }

            PasswordResetToken resetToken = resetTokenOpt.get();
            user.setPasswordHash(passwordEncoder.encode(resetToken.getResetToken()));

            passwordResetTokenRepository.delete(resetToken);
            userRepository.save(user);
        }

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(customUserDetails);
        return ResultWithValue.success(new TokenDTO(token));
    }

    @Override
    @Transactional
    @Auditable(action = "RESEND_CONFIRMATION_CODE")
    public ResultWithoutValue resendConfirmationCode(String email) {
        Optional<User> userOpt = userRepository.findByEmailAndEmailConfirmedFalse(email);
        if (userOpt.isEmpty()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        User user = userOpt.get();

        Optional<EmailConfirmation> confirmationOpt = emailConfirmationRepository.findByUserUserId(user.getUserId());
        if (confirmationOpt.isEmpty()) {
            return ResultWithoutValue.failure(UserErrors.EMAIL_CONFIRMATION_NOT_FOUND);
        }

        EmailConfirmation confirmation = confirmationOpt.get();
        confirmation.setConfirmationCode(UUID.randomUUID().toString());
        confirmation.setExpiresAt(Instant.now().plusSeconds(24 * 3600));
        emailConfirmationRepository.save(confirmation);

        String link = frontendUrl + "/confirm-email?code=" + confirmation.getConfirmationCode();
        emailService.sendEmail(user.getEmail(), "Confirm Your Email", "Click here: " + link);

        return ResultWithoutValue.success();
    }

    @Override
    @Transactional
    @Auditable(action = "CREATE_USER")
    public ResultWithoutValue createUser(UserCreateDTO dto, Short tokenUserId) {
        Optional<User> createdByOpt = userRepository.findById(tokenUserId);
        if (createdByOpt.isEmpty()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            return ResultWithoutValue.failure(UserErrors.USER_EXISTS);
        }

        User createdBy = createdByOpt.get();

        if (dto.getRoles() == null || dto.getRoles().isEmpty())
            return ResultWithoutValue.failure(UserErrors.NO_ROLES_SPECIFIED);

        List<Role> roles = roleRepository.findByRoleIdIn(dto.getRoles());
        if (roles.size() != dto.getRoles().size())
            return ResultWithoutValue.failure(UserErrors.INVALID_ROLES);

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setBio(dto.getBio());
        user.setEmailConfirmed(true);
        user.setIsActive(true);
        List<UserRole> userRoles = roles.stream()
                .map(role -> {
                    UserRole ur = new UserRole();
                    ur.setUser(user);
                    ur.setRole(role);
                    ur.setId(new UserRoleId(user.getUserId(), role.getRoleId()));
                    return ur;
                })
                .toList();
        user.setUserRoles(userRoles);
        if (dto.getImage() != null) {
            try {
                user.setImage(dto.getImage().getBytes());
            } catch (IOException e) {
                log.error("Failed to read user image for user name={}", user.getFirstName() + File.separator + user.getLastName(), e);
                return ResultWithoutValue.failure(ValidationErrors.IMAGE_READ_ERROR);
            }
        }
        else
            user.setImage(null);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);

        Message message = Message.builder()
                .user(user)
                .createdAt(Instant.now())
                .content(String.format("User %s %s created a user %s %s.", createdBy.getFirstName(), createdBy.getLastName(), user.getFirstName(), user.getLastName())).build();
        messageRepository.save(message);

        return ResultWithoutValue.success();
    }

    @Override
    public ResultWithValue<List<EmployeeDropdownDTO>> getEmployeeForDropdown() {
        List<User> employees = userRepository.findByRoleName(RoleEnum.EMPLOYEE.getRoleName());
        List<EmployeeDropdownDTO> dtos = employees.stream()
                .map(u -> new EmployeeDropdownDTO(u.getUserId(), u.getFirstName(), u.getLastName()))
                .collect(Collectors.toList());
        return ResultWithValue.success(dtos);
    }

    @Override
    public ResultWithValue<PagedUsersDTO> getUsersForAdmin(int page, int rowsPerPage, String search) {
        if (page < 1 || rowsPerPage < 1)
            return ResultWithValue.failure(ValidationErrors.NUMBER_OF_PAGES);

        var usersQuery = userRepository.findAllByIsActiveTrue();
        long totalCount = usersQuery.size();

        List<EmployeeAdminDTO> employees = usersQuery.stream()
                .skip((long) (page - 1) * rowsPerPage)
                .limit(rowsPerPage)
                .map(u -> new EmployeeAdminDTO(u.getUserId(), u.getFirstName(), u.getLastName(), u.getPhoneNumber(), u.getBio(),
                        u.getEmail(), u.getImage(), u.getRoles().stream().map(Role::getName).toList()))
                .collect(Collectors.toList());

        return ResultWithValue.success(new PagedUsersDTO(totalCount, employees));
    }

    @Override
    public ResultWithValue<List<EmployeeDetailsDTO>> getEmployees() {
        List<User> employees = userRepository.findByRoleName(RoleEnum.EMPLOYEE.getRoleName());
        List<EmployeeDetailsDTO> dtos = employees.stream()
                .map(u -> new EmployeeDetailsDTO(u.getFirstName() + " " + u.getLastName(), u.getBio(), u.getEmail(), u.getImage()))
                .collect(Collectors.toList());
        return ResultWithValue.success(dtos);
    }

    @Override
    @Transactional
    @Auditable(action = "DELETE_USER")
    public ResultWithoutValue deleteUser(Short userId, Short tokenUserId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<User> tokenUserOpt = userRepository.findById(tokenUserId);

        if (userOpt.isEmpty() || tokenUserOpt.isEmpty())
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        User user = userOpt.get();
        User tokenUser = tokenUserOpt.get();

        if (!user.getIsActive() || !tokenUser.getIsActive())
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);

        user.setIsActive(false);
        userRepository.save(user);

        Message message = Message.builder()
                .user(user)
                .createdAt(Instant.now())
                .content(String.format("User %s %s deleted a user %s %s.", tokenUser.getFirstName(), tokenUser.getLastName(), user.getFirstName(), user.getLastName())).build();
        messageRepository.save(message);

        return ResultWithoutValue.success();
    }

    @Override
    @Transactional
    @Auditable(action = "UPDATE_USER")
    public ResultWithoutValue updateUser(Short userId, UserUpdateDTO dto, Short tokenUserId) {
        Optional<User> existingUserOpt = userRepository.findByUserIdWithRoles(userId);
        Optional<User> tokenUserOpt = userRepository.findById(tokenUserId);

        if (existingUserOpt.isEmpty() || tokenUserOpt.isEmpty())
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        User existingUser = existingUserOpt.get();
        User tokenUser = tokenUserOpt.get();

        if (!existingUser.getIsActive() || !tokenUser.getIsActive())
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);

        existingUser.setFirstName(dto.getFirstName());
        existingUser.setLastName(dto.getLastName());
        existingUser.setEmail(dto.getEmail());
        existingUser.setPhoneNumber(dto.getPhoneNumber());
        existingUser.setBio(dto.getBio());
        if (dto.getImage() != null) {
            try {
                existingUser.setImage(dto.getImage().getBytes());
            } catch (IOException e) {
                log.error("Failed to read user image for user name={}", existingUser.getFirstName() + File.separator + existingUser.getLastName(), e);
                return ResultWithoutValue.failure(ValidationErrors.IMAGE_READ_ERROR);
            }
        }

        List<Role> newRoles = roleRepository.findByRoleIdIn(dto.getRoles());
        if (newRoles.size() != dto.getRoles().size())
            return ResultWithoutValue.failure(UserErrors.INVALID_ROLES);

        if (newRoles.isEmpty())
            return ResultWithoutValue.failure(UserErrors.NO_ROLES_SPECIFIED);

        Set<Short> newRoleIds = newRoles.stream().map(Role::getRoleId).collect(Collectors.toSet());
        existingUser.getUserRoles().removeIf(userRole -> !newRoleIds.contains(userRole.getRole().getRoleId()));

        Set<Short> existingRoleIds = existingUser.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getRoleId())
                .collect(Collectors.toSet());

        newRoles.stream()
                .filter(role -> !existingRoleIds.contains(role.getRoleId()))
                .map(role -> {
                    UserRole ur = new UserRole();
                    ur.setUser(existingUser);
                    ur.setRole(role);
                    ur.setId(new UserRoleId(existingUser.getUserId(), role.getRoleId()));
                    return ur;
                })
                .forEach(existingUser.getUserRoles()::add);

        userRepository.save(existingUser);

        Message message = Message.builder()
                .user(tokenUser)
                .createdAt(Instant.now())
                .content(String.format("User %s %s updated a user %s %s.", tokenUser.getFirstName(), tokenUser.getLastName(), dto.getFirstName(), dto.getLastName())).build();
        messageRepository.save(message);

        return ResultWithoutValue.success();
    }

    @Override
    @Transactional
    @Auditable(action = "UPDATE_PASSWORD")
    public ResultWithoutValue updatePassword(Short tokenUserId, PasswordUpdateDTO dto) {
        Optional<User> userOpt = userRepository.findById(tokenUserId);
        if (userOpt.isEmpty())
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);

        User user = userOpt.get();
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPasswordHash())) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        return ResultWithoutValue.success();
    }
}