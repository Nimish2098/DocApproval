package com.Project.DocApproval.common.exception;

import com.Project.DocApproval.analysis.exception.InvalidJobDescriptionException;
import com.Project.DocApproval.application.exception.DuplicateApplicationException;
import com.Project.DocApproval.common.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class    GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(new ErrorResponse("VALIDATION_ERROR", message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        return new ResponseEntity<>(
                new ErrorResponse("UNAUTHORIZED", "Invalid email or password"),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        HttpStatusCode status = ex.getStatusCode();
        String message = ex.getReason() == null ? "Request failed" : ex.getReason();
        return new ResponseEntity<>(new ErrorResponse("REQUEST_ERROR", message), status);
    }


    @ExceptionHandler(InvalidJobDescriptionException.class)
    public ResponseEntity<ErrorResponse> handleJD(InvalidJobDescriptionException ex) {
        return new ResponseEntity<>(new ErrorResponse("INVALID_JD", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    // NEW: Handle ResourceNotFound (User not found, Resume not found)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse("NOT_FOUND", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    // NEW: Handle Duplicate Applications (The "Circuit Breaker" we discussed)
    @ExceptionHandler(DuplicateApplicationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateApplicationException ex) {
        return new ResponseEntity<>(new ErrorResponse("ALREADY_EXISTS", "This record already exists in the system."), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSize(MaxUploadSizeExceededException ex) {
        return new ResponseEntity<>(new ErrorResponse("FILE_TOO_LARGE", "Maximum upload size is 5MB."), HttpStatus.PAYLOAD_TOO_LARGE);
    }

    // Generic fallback for any other RuntimeExceptions (like AnalysisException)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleGeneral(RuntimeException ex) {
        return new ResponseEntity<>(new ErrorResponse("SERVER_ERROR", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}