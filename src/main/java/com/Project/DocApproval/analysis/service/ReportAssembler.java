package com.Project.DocApproval.analysis.service;

// ReportAssembler.java
import java.util.*;
import org.springframework.stereotype.Component;
import com.Project.DocApproval.analysis.ai.*;
import com.Project.DocApproval.analysis.check.CheckResult;
import com.Project.DocApproval.analysis.dto.*;

@Component
public class ReportAssembler {
  private static final int MAX_ISSUES = 15;

  private final ScoreCalculator calculator;
  private final EvidenceValidator validator;

  public ReportAssembler(ScoreCalculator calculator, EvidenceValidator validator) {
    this.calculator = calculator;
    this.validator = validator;
  }

  public ResumeReport assemble(List<CheckResult> checks, AiAssessment ai, String resumeText) {
    List<ParameterScore> parameters = calculator.blend(checks, ai);

    List<Issue> issues = new ArrayList<>();
    checks.forEach(c -> issues.addAll(c.issues()));
    issues.addAll(validator.validate(ai.issues(), resumeText));
    issues.sort(Comparator.comparing(Issue::severity)); // HIGH first; stable within a severity

    return new ResumeReport(ai.summary(), parameters,
                            issues.stream().limit(MAX_ISSUES).toList(),
                            ai.missingMaterial(), ai.actionPlan());
  }

  public int overall(ResumeReport report) {
    return calculator.overall(report.parameters());
  }
}