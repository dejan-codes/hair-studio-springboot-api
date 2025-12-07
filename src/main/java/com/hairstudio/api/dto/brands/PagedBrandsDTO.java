package com.hairstudio.api.dto.brands;

import java.util.List;

public record PagedBrandsDTO(long totalCount, List<BrandDTO> brands) {}