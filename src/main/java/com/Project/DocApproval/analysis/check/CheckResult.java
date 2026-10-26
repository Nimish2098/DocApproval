package com.Project.DocApproval.analysis.check;


// CheckResult.java
import com.Project.DocApproval.analysis.dto.AnalysisParameter;
import com.Project.DocApproval.analysis.dto.Issue;
import com.Project.DocApproval.analysis.dto.ParameterScore;

import java.util.List;

public record CheckResult(ParameterScore score, List<Issue> issues) {
  public CheckResult {
    issues = issues == null ? List.of() : List.copyOf(issues);
  }
  public AnalysisParameter parameter() { return score.parameter(); }
}