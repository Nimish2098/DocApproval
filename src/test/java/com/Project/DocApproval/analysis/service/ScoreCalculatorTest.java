package com.Project.DocApproval.analysis.service;

// ScoreCalculatorTest.java
import java.util.List;
import org.junit.jupiter.api.Test;
import com.Project.DocApproval.analysis.ai.AiAssessment;
import com.Project.DocApproval.analysis.check.CheckResult;
import com.Project.DocApproval.analysis.dto.*;
import static com.Project.DocApproval.analysis.dto.AnalysisParameter.*;
import static org.junit.jupiter.api.Assertions.*;

class ScoreCalculatorTest {
  private final ScoreCalculator calc = new ScoreCalculator();

  private ParameterScore s(AnalysisParameter p, int v) { return new ParameterScore(p, v, p.weight(), "x"); }
  private CheckResult c(AnalysisParameter p, int v) { return new CheckResult(s(p, v), List.of()); }

  @Test
  void blendsAndWeights() {
    List<CheckResult> checks = List.of(c(STRUCTURE_ATS, 100), c(CONTACT_LINKS, 100),
                                       c(PROJECTS, 100), c(SKILLS, 50), c(WRITING_QUALITY, 100));
    AiAssessment ai = new AiAssessment("sum",
                                       List.of(s(PROJECTS, 50), s(SKILLS, 100), s(WRITING_QUALITY, 100), s(HIDDEN_MATERIAL, 40)),
                                       List.of(), List.of(), List.of());

    List<ParameterScore> out = calc.blend(checks, ai);

    assertEquals(6, out.size());
    assertEquals(70, out.get(PROJECTS.ordinal()).score());
    assertEquals(80, out.get(SKILLS.ordinal()).score());
    assertEquals(40, out.get(HIDDEN_MATERIAL.ordinal()).score());
    assertEquals(81, calc.overall(out));
    assertEquals(ScoreLabel.STRONG_FOR_FRESHER, ScoreLabel.fromScore(81));
  }

  @Test
  void missingAiScoreThrows() {
    List<CheckResult> checks = List.of(c(STRUCTURE_ATS, 100), c(CONTACT_LINKS, 100),
                                       c(PROJECTS, 100), c(SKILLS, 50), c(WRITING_QUALITY, 100));
    AiAssessment ai = new AiAssessment("sum", List.of(s(PROJECTS, 50)), List.of(), List.of(), List.of());
    assertThrows(IllegalStateException.class, () -> calc.blend(checks, ai));
  }
}