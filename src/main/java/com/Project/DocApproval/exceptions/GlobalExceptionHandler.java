package com.Project.DocApproval.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidFileFormatException.class)
    public ResponseEntity<ErrorResponse> handleFileFormat(InvalidFileFormatException ex) {
        return new ResponseEntity<>(new ErrorResponse("INVALID_FILE", ex.getMessage()), HttpStatus.BAD_REQUEST);
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