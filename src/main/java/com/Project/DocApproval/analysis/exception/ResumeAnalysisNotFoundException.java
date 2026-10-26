package com.Project.DocApproval.analysis.exception;


public class ResumeAnalysisNotFoundException extends RuntimeException {

  public ResumeAnalysisNotFoundException(Long id) {
    super("Resume analysis not found: " + id);
  }
}