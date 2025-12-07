package com.hairstudio.api.errors;

import com.hairstudio.api.common.Error;

public final class ProductErrors {
    public static final Error PRODUCT_NOT_FOUND = new Error(
            "Product.ProductNotFound", "Product not found.");
}