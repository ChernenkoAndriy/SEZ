package com.andruf.sez.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class SezException extends RuntimeException {
    private final String code;
    private final HttpStatus status;

    public SezException(String message, String code, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }
}

