package com.Project.DocApproval.analysis.check;

import java.util.List;

import com.Project.DocApproval.analysis.check.CheckResult;
import com.Project.DocApproval.analysis.dto.*;

final class CheckSupport {
  private CheckSupport() {}

  static Issue issue(AnalysisParameter p, Severity s, String evidence, String problem, String fix) {
    return new Issue(p, s, IssueSource.CODE, evidence, problem, fix, null);
  }

  static CheckResult result(AnalysisParameter p, int score, String summary, List<Issue> issues) {
    int clamped = Math.max(0, Math.min(100, score));
    return new CheckResult(new ParameterScore(p, clamped, p.weight(), summary), issues);
  }
}