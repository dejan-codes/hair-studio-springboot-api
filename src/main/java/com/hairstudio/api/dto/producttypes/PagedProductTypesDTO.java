package com.hairstudio.api.dto.producttypes;

import java.util.List;

public record PagedProductTypesDTO(long totalCount, List<ProductTypeDTO> productTypes) {}
