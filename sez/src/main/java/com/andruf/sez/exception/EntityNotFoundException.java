package com.andruf.sez.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends SezException {
    public EntityNotFoundException(String message) {
        super(message, "ENTITY_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
