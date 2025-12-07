package com.hairstudio.api.common;

import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public class ResultWithValue<T> extends Result {
    private final T value;

    private ResultWithValue(T value) {
        super(true, Error.NONE);
        this.value = value;
    }

    private ResultWithValue(Error error) {
        super(false, error);
        this.value = null;
    }

    public static <T> ResultWithValue<T> success(T value) {
        return new ResultWithValue<>(value);
    }

    public static <T> ResultWithValue<T> failure(Error error) {
        return new ResultWithValue<>(error);
    }

    @Override
    public ResponseEntity<?> toResponseEntity() {
        if (isSuccess()) {
            return ResponseEntity.ok(value);
        } else {
            return ResponseEntity.status(ErrorHttpMapper.getStatus(getError()))
                    .body(getError().description());
        }
    }
}