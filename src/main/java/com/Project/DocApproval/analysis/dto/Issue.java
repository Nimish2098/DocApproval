package com.Project.DocApproval.analysis.dto;

public record Issue(
    AnalysisParameter parameter,
    Severity severity,
    IssueSource source,
    String evidence,    // exact quote from the resume; null only for CODE issues about something missing
    String problem,     // one sentence
    String fix,         // what to do
    String rewrite      // improved version of the quoted line, using only facts from the resume; nullable
) {}