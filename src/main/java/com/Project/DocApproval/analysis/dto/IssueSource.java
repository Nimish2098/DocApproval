package com.Project.DocApproval.analysis.dto;

/** Who found the issue. CODE issues may have no quote (e.g. "no GitHub link"). AI issues must quote the resume. */
public enum IssueSource { CODE, AI }