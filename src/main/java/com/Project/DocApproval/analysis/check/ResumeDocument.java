package com.Project.DocApproval.analysis.check;

// ResumeDocument.java  (pageCount = 0 means unknown; the page check is then skipped)
public record ResumeDocument(String text, int pageCount) {}