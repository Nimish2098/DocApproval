package com.Project.DocApproval.analysis.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;

@RestControllerAdvice
public class ApiExceptionHandler {
  public record ApiError(String error, String message) {}

  @ExceptionHandler(InvalidRequestException.class)
  ResponseEntity<ApiError> invalid(InvalidRequestException ex) {
    return ResponseEntity.badRequest().body(new ApiError("INVALID_REQUEST", ex.getMessage()));
  }

  @ExceptionHandler(ResumeAnalysisNotFoundException.class)
  ResponseEntity<ApiError> notFound(ResumeAnalysisNotFoundException ex) {
    return ResponseEntity.status(404).body(new ApiError("NOT_FOUND", "Analysis not found."));
  }

  @ExceptionHandler(AnalysisUnavailableException.class)
  ResponseEntity<ApiError> unavailable(AnalysisUnavailableException ex) {
    return ResponseEntity.status(503).body(new ApiError("AI_UNAVAILABLE",
                                                        "The analysis service is busy. Please try again in a minute."));
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  ResponseEntity<ApiError> tooLarge(MaxUploadSizeExceededException ex) {
    return ResponseEntity.status(413).body(new ApiError("FILE_TOO_LARGE", "Please upload a file under 2 MB."));
  }

  @ExceptionHandler(IOException.class)
  ResponseEntity<ApiError> unreadable(IOException ex) {
    return ResponseEntity.unprocessableEntity().body(new ApiError("UNREADABLE_FILE",
                                                                  "We couldn't read this file. Try exporting it again as a PDF or DOCX."));
  }
}