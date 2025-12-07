package com.hairstudio.api.dto.products;

import java.util.List;

public record PagedProductsDTO(long totalCount, List<ProductDisplayDTO> products) {}
