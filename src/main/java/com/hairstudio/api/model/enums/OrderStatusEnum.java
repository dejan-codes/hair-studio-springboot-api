package com.hairstudio.api.model.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {
    Pending((short) 1),
    Sent((short) 2),
    Rejected((short) 3);

    private final Short code;

    OrderStatusEnum(short code) {
        this.code = code;
    }
}