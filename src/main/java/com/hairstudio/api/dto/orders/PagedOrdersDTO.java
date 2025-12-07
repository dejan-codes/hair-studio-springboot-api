package com.hairstudio.api.dto.orders;

import java.util.List;

public record PagedOrdersDTO(long totalCount, List<OrderSummaryDTO> orders) {}
