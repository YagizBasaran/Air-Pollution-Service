package com.weather.WeatherApp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(InvalidDateException.class)
        public ResponseEntity<Map<String, Object>> handleInvalidDateException(InvalidDateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        }
    }
