package com.andruf.sez.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends SezException {
    public BusinessException(String message, String code) {
        super(message, code, HttpStatus.BAD_REQUEST);
    }
}
