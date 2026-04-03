package com.Project.DocApproval.enums;

public enum ResumeStatus {




    UPLOADED,   // Initial file storage
    PARSING,    // Text extraction
    ANALYZING,  // Keyword comparison
    COMPLETED,  // Results finalized
    FAILED
}