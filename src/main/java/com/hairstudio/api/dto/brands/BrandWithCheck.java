package com.hairstudio.api.dto.brands;

import com.hairstudio.api.model.entity.Brand;

public record BrandWithCheck(Brand brand, boolean hasActiveProducts) {}