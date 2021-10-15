package com.sarbesh.core.controller;

import com.sarbesh.core.dto.ErrorResponse;
import com.sarbesh.core.dto.ErrorType;
import com.sarbesh.core.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler({Exception.class,RuntimeException.class})
    public ResponseEntity<ErrorResponse> exceptionController(Exception ex){
        LOGGER.error("#ExceptionController-exceptionController Returning error response {} due to {}",ex.getMessage(),ex.getCause());
        ErrorResponse errorResponse = new ErrorResponse(ErrorType.ERROR, "500", ex.getClass().getName(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorResponse> badRequestController(Exception ex){
        LOGGER.error("ExceptionController-badRequestController Returning error response {} due to {}",ex.getMessage(),ex.getCause());
        ErrorResponse errorResponse = new ErrorResponse(ErrorType.WARN, "500", ex.getClass().getName(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
