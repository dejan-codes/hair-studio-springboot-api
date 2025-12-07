package com.hairstudio.api.dto.messages;

import java.time.Instant;

public record MessageTableDTO(String message, Instant date) {}