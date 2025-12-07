package com.hairstudio.api.model.enums;

import lombok.Getter;

@Getter
public enum GenderEnum {
    MALE(1),
    FEMALE(2);

    private final int code;

    GenderEnum(int code) {
        this.code = code;
    }
}