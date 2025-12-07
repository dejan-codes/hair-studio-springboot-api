package com.hairstudio.api.errors;

import com.hairstudio.api.common.Error;

public final class PaymentStatusErrors {
    public static final Error PAYMENT_STATUS_NOT_FOUND = new Error(
            "PaymentStatus.PaymentStatusNotFound", "Payment status not found.");
}