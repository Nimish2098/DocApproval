package com.Project.DocApproval.analysis.ai;

// ResumeAiAnalyzerTest.java  (in the ai test package)
import java.util.List;
import org.junit.jupiter.api.Test;
import com.Project.DocApproval.analysis.dto.*;
import static com.Project.DocApproval.analysis.dto.AnalysisParameter.*;
import static org.junit.jupiter.api.Assertions.*;

class ResumeAiAnalyzerTest {
  private ParameterScore s(AnalysisParameter p, int v) { return new ParameterScore(p, v, 0, "x"); }

  @Test
  void keepsOnlyAiParametersAndForcesSourceAi() {
    AiAssessment raw = new AiAssessment("Summary",
                                        List.of(s(PROJECTS, 150), s(SKILLS, 60), s(WRITING_QUALITY, 70), s(HIDDEN_MATERIAL, 40), s(STRUCTURE_ATS, 90)),
                                        List.of(new Issue(PROJECTS, Severity.HIGH, IssueSource.CODE, "Library System", "Vague", "Add detail", null),
                                                new Issue(PROJECTS, Severity.HIGH, IssueSource.AI, "  ", "No quote", "x", null)),
                                        List.of(),
                                        List.of(new ActionPlanStep(5, "Add GitHub link", "d", "1 day", Impact.HIGH)));

    AiAssessment out = ResumeAiAnalyzer.sanitize(raw);

    assertEquals(4, out.scores().size());
    assertEquals(100, out.scores().stream().filter(x -> x.parameter() == PROJECTS).findFirst().get().score());
    assertEquals(1, out.issues().size());
    assertEquals(IssueSource.AI, out.issues().get(0).source());
    assertEquals(1, out.actionPlan().get(0).priority());
  }

  @Test
  void missingParameterThrowsSoTheCallerRetries() {
    AiAssessment raw = new AiAssessment("x", List.of(s(PROJECTS, 50)), List.of(), List.of(),
                                        List.of(new ActionPlanStep(1, "t", "d", "1 day", Impact.LOW)));
    assertThrows(IllegalStateException.class, () -> ResumeAiAnalyzer.sanitize(raw));
  }
}