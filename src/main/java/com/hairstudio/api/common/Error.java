package com.hairstudio.api.common;

public record Error(String code, String description) {
    public static final Error NONE = new Error("", "");
}