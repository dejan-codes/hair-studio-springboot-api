package com.hairstudio.api.errors;

import com.hairstudio.api.common.Error;

public final class OrderErrors {
    public static final Error ORDER_NOT_FOUND = new Error(
            "Order.OrderNotFound", "Order not found.");

    public static final Error ORDER_STATUS_NOT_FOUND = new Error(
            "Order.OrderStatusNotFound", "Order status not found.");
}