package com.Project.DocApproval.enums;
public enum ApplicationStatus {
    UPLOADED,           // File received
    ANALYZING,          // Text extraction in progress
    CRITIQUING,         // Comparing Resume vs JD
    FEEDBACK_GENERATED, // Critique saved to DB
    REJECTED_WITH_MAIL, // Honesty report sent
    FAILED_PROCESSING   // Something went wrong (Exception handled)
}