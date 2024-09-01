package com.aurionpro.bank.exceptions;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.aurionpro.bank.error.ErrorResponse;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;

@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ErrorResponse createErrorResponse(HttpStatus status, List<String> messages) {
        return new ErrorResponse(
            status.value(),
            messages,
            LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        );
    }

  
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        List<String> errorMessages = violations.stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.toList());

        logger.error("ConstraintViolationException: {}", errorMessages, ex);

        ErrorResponse errorResponse = createErrorResponse(HttpStatus.BAD_REQUEST, errorMessages);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        logger.error("ValidationException: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = createErrorResponse(HttpStatus.BAD_REQUEST, List.of(ex.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        logger.error("EntityNotFoundException: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = createErrorResponse(HttpStatus.NOT_FOUND, List.of(ex.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.error("HttpMessageNotReadableException: Malformed JSON request or invalid data", ex);

        ErrorResponse errorResponse = createErrorResponse(HttpStatus.BAD_REQUEST, List.of("Malformed JSON request or invalid data"));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UserApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleUserApiException(UserApiException ex) {
        logger.error("UserApiException: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = createErrorResponse(HttpStatus.BAD_REQUEST, List.of(ex.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Exception: An unexpected error occurred", ex);

        ErrorResponse errorResponse = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            List.of("An unexpected error occurred.")
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
