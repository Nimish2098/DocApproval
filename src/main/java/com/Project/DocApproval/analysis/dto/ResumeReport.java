package com.Project.DocApproval.analysis.dto;

import java.util.List;

public record ResumeReport(
    String summary,
    List<ParameterScore> parameters,
    List<Issue> issues,
    List<MissingMaterial> missingMaterial,
    List<ActionPlanStep> actionPlan
) {
  public ResumeReport {
    parameters = parameters == null ? List.of() : List.copyOf(parameters);
    issues = issues == null ? List.of() : List.copyOf(issues);
    missingMaterial = missingMaterial == null ? List.of() : List.copyOf(missingMaterial);
    actionPlan = actionPlan == null ? List.of() : List.copyOf(actionPlan);
  }
}