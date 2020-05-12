package com.pingxin.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice

public class IOExceptionHandler
        extends IOException {

    @ExceptionHandler(value = IOException.class)
    protected ResponseEntity<String> handleConflict() {
            return new ResponseEntity<>("Hello World!", HttpStatus.UNAUTHORIZED);
    }
}