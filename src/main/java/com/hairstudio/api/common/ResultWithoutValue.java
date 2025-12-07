package com.hairstudio.api.common;

import org.springframework.http.ResponseEntity;

public class ResultWithoutValue extends Result {
    private ResultWithoutValue() {
        super(true, Error.NONE);
    }

    private ResultWithoutValue(Error error) {
        super(false, error);
    }

    public static ResultWithoutValue success() { return new ResultWithoutValue(); }
    public static ResultWithoutValue failure(Error error) { return new ResultWithoutValue(error); }

    @Override
    public ResponseEntity<?> toResponseEntity() {
        if (isSuccess()) return ResponseEntity.ok().build();
        return ResponseEntity.status(ErrorHttpMapper.getStatus(getError()))
                .body(getError().description());
    }
}