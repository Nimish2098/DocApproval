package com.Project.DocApproval.analysis.ai;
import com.Project.DocApproval.analysis.dto.ActionPlanStep;
import com.Project.DocApproval.analysis.dto.Issue;
import com.Project.DocApproval.analysis.dto.MissingMaterial;
import com.Project.DocApproval.analysis.dto.ParameterScore;

import java.util.List;

public record AiAssessment(
    String summary,
    List<ParameterScore> scores,          // only PROJECTS, SKILLS, WRITING_QUALITY, HIDDEN_MATERIAL
    List<Issue> issues,                   // source = AI, evidence required
    List<MissingMaterial> missingMaterial,
    List<ActionPlanStep> actionPlan
) {
  public AiAssessment {
    scores = scores == null ? List.of() : List.copyOf(scores);
    issues = issues == null ? List.of() : List.copyOf(issues);
    missingMaterial = missingMaterial == null ? List.of() : List.copyOf(missingMaterial);
    actionPlan = actionPlan == null ? List.of() : List.copyOf(actionPlan);
  }
}