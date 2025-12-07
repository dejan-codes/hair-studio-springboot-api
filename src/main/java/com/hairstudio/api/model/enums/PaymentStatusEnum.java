package com.hairstudio.api.model.enums;

import lombok.Getter;

@Getter
public enum PaymentStatusEnum {
    UNPAID((short) 1),
    PAID((short) 2);

    private final Short code;

    PaymentStatusEnum(short code) {
        this.code = code;
    }
}