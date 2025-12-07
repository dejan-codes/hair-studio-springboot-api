package com.hairstudio.api.common;

import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public abstract class Result {
    private final boolean success;
    private final Error error;

    protected Result(boolean success, Error error) {
        if ((success && error != Error.NONE) || (!success && error == Error.NONE)) {
            throw new IllegalArgumentException("Invalid error state");
        }
        this.success = success;
        this.error = error;
    }

    public boolean isFailure() { return !success; }

    public abstract ResponseEntity<?> toResponseEntity();
}
