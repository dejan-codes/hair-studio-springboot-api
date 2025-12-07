package com.hairstudio.api.errors;

import com.hairstudio.api.common.Error;

public final class ProductTypeErrors {
    public static final Error PRODUCT_TYPE_NOT_FOUND = new Error(
            "ProductType.ProductTypeNotFound", "Product type not found.");

    public static final Error PRODUCT_TYPE_HAS_PRODUCT = new Error(
            "ProductType.ProductTypeHasProduct", "Product type cannot be deleted because it is referenced by a product.");

    public static final Error PRODUCT_TYPE_EXISTS = new Error(
            "ProductType.ProductTypeExists", "Product type with that name already exists.");
}