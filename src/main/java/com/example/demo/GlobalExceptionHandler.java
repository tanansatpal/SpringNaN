package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleException(Exception ex, WebRequest request){
        return ResponseEntity.status(500).body(new CustomErrorResponse("This is not working", request.getDescription(false), 500));
    }
}
