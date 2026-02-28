package com.Project.DocApproval.enums;

public enum ApplicationStatus {
    UPLOADED,   // Initial file storage
    PARSING,    // Text extraction
    ANALYZING,  // Keyword comparison
    COMPLETED,  // Results finalized
    FAILED      // Error handling
}