package com.hairstudio.api.service;

import com.hairstudio.api.common.ResultWithValue;
import com.hairstudio.api.dto.messages.PagedMessagesDTO;

public interface MessageService {
     ResultWithValue<PagedMessagesDTO> getMessages(short tokenUserId, int page, int rowsPerPage);
}