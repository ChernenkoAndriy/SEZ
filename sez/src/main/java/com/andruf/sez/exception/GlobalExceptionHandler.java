package com.andruf.sez.exception;

import com.andruf.sez.gendto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.Arrays;
@Slf4j // Додає логер для виведення в консоль
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SezException.class)
    public ResponseEntity<ErrorResponse> handleSezException(SezException ex) {
        // Виводимо в консоль повідомлення та код
        log.error("Business Exception [{}]: {}", ex.getCode(), ex.getMessage());

        ErrorResponse error = new ErrorResponse();
        error.setMessage(ex.getMessage());
        error.setCode(ex.getCode());
        return new ResponseEntity<>(error, ex.getStatus());
    }
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        ErrorResponse error = new ErrorResponse();
        error.setMessage(message);
        error.setCode("VALIDATION_ERROR");
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("Server Exception [{}]: {}", ex.getMessage(), ex.getStackTrace());
        Throwable rootCause = org.springframework.core.NestedExceptionUtils.getMostSpecificCause(ex);
        log.error("ROOT CAUSE: {}", rootCause.getMessage());
        ErrorResponse error = new ErrorResponse();
        error.setMessage(ex.getMessage());
        error.setCode("GENERAL_ERROR");
        return ResponseEntity.internalServerError().body(error);
    }
}