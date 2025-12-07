package com.hairstudio.api.errors;

import com.hairstudio.api.common.Error;

public final class BrandErrors {
    public static final Error BRAND_NOT_FOUND = new Error(
            "Brand.BrandNotFound", "Brand not found.");

    public static final Error BRAND_HAS_PRODUCT = new Error(
            "Brand.BrandHasProduct", "Brand cannot be deleted because it is referenced by a product.");

    public static final Error BRAND_EXISTS = new Error(
            "Brand.BrandExists", "Brand with that name already exists.");
}