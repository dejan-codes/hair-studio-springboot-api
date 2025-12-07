package com.hairstudio.api.dto.orders;

import java.time.LocalDateTime;
import java.util.List;

public record OrderSummaryDTO(int orderId, String fullName, short orderStatusId, String paymentStatus, LocalDateTime paidAt, List<OrderItemDetailDTO> ordersDTO) {}