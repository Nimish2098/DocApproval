package com.Project.DocApproval.analysis.dto;

public record MissingMaterial(
    String type,          // e.g. "Coursework", "Certifications", "College mini-project"
    String whyItMatters,
    String howToAdd
) {}