package com.hairstudio.api.service;

import com.hairstudio.api.common.ResultWithValue;
import com.hairstudio.api.dto.messages.MessageTableDTO;
import com.hairstudio.api.dto.messages.PagedMessagesDTO;
import com.hairstudio.api.model.enums.RoleEnum;
import com.hairstudio.api.errors.UserErrors;
import com.hairstudio.api.errors.ValidationErrors;
import com.hairstudio.api.model.entity.Message;
import com.hairstudio.api.model.entity.Role;
import com.hairstudio.api.repository.MessageRepository;
import com.hairstudio.api.repository.UserRepository;
import com.hairstudio.api.security.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {

    private final CurrentUserContext currentUserContext;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public ResultWithValue<PagedMessagesDTO> getMessages(int page, int rowsPerPage) {
        if (page < 1 || rowsPerPage < 1) {
            return ResultWithValue.failure(ValidationErrors.NUMBER_OF_PAGES);
        }

        var userOpt = userRepository.findByUserIdWithRoles(currentUserContext.getUserId());
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithValue.failure(UserErrors.USER_NOT_FOUND);
        }

        var user = userOpt.get();
        var roleNames = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        PageRequest pageable = PageRequest.of(page - 1, rowsPerPage);

        Page<Message> messagesPage;
        long totalMessages;
        if (!roleNames.contains(RoleEnum.EMPLOYEE.getRoleName()) && !roleNames.contains(RoleEnum.ADMINISTRATOR.getRoleName())) {
            totalMessages = messageRepository.countByUserUserId(user.getUserId());
            messagesPage = messageRepository.findByUserUserId(user.getUserId(), pageable);
        } else {
            totalMessages = messageRepository.count();
            messagesPage = messageRepository.findAll(pageable);
        }

        var messagesForTable = messagesPage.getContent().stream()
                .map(m -> new MessageTableDTO(m.getContent(), m.getCreatedAt()))
                .toList();

        return ResultWithValue.success(new PagedMessagesDTO(totalMessages, messagesForTable));
    }
}