package com.hairstudio.api.dto.producttypes;

import com.hairstudio.api.model.entity.ProductType;

public record ProductTypeWithCheck(ProductType productType, boolean hasActiveProducts) {}