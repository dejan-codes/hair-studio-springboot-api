package com.hairstudio.api.dto.messages;

import java.util.List;

public record PagedMessagesDTO(long totalCount, List<MessageTableDTO> messages) {}