package com.Project.DocApproval.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles InvalidFileFormatException (Thrown in validateFile())
    @ExceptionHandler(InvalidFileFormatException.class)
    public ResponseEntity<ErrorResponse> handleFileFormat(InvalidFileFormatException ex) {
        return new ResponseEntity<>(new ErrorResponse("INVALID_FILE", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    // Handles InvalidJobDescriptionException (Thrown in analyzeResume())
    @ExceptionHandler(InvalidJobDescriptionException.class)
    public ResponseEntity<ErrorResponse> handleJD(InvalidJobDescriptionException ex) {
        return new ResponseEntity<>(new ErrorResponse("INVALID_JD", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    // Handles MaxUploadSizeExceededException (Thrown automatically by Spring)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSize(MaxUploadSizeExceededException ex) {
        return new ResponseEntity<>(new ErrorResponse("FILE_TOO_LARGE", "Maximum upload size is 5MB."), HttpStatus.PAYLOAD_TOO_LARGE);
    }
}