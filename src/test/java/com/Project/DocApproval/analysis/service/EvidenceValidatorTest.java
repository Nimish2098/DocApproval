package com.Project.DocApproval.analysis.service;

// EvidenceValidatorTest.java
import java.util.List;
import org.junit.jupiter.api.Test;
import com.Project.DocApproval.analysis.dto.*;
import static org.junit.jupiter.api.Assertions.*;

class EvidenceValidatorTest {
  private final EvidenceValidator v = new EvidenceValidator();
  private static final String RESUME = """
            Projects
            • Built a REST API with Spring Boot for 50 test users.
            • Worked on the login page.
            """;

  private Issue ai(String evidence, String rewrite) {
    return new Issue(AnalysisParameter.PROJECTS, Severity.MEDIUM, IssueSource.AI,
                     evidence, "problem", "fix", rewrite);
  }

  @Test
  void keepsQuoteThatExistsDespiteBulletAndPunctuationDifferences() {
    assertEquals(1, v.validate(List.of(ai("Built a REST API with Spring Boot for 50 test users", null)), RESUME).size());
  }

  @Test
  void dropsInventedQuote() {
    assertTrue(v.validate(List.of(ai("Led a team of ten engineers at Google", null)), RESUME).isEmpty());
  }

  @Test
  void dropsTooShortEvidence() {
    assertTrue(v.validate(List.of(ai("Java", null)), RESUME).isEmpty());
  }

  @Test
  void removesRewriteWithInventedNumberButKeepsIssue() {
    List<Issue> out = v.validate(List.of(ai("Worked on the login page",
                                            "Built a login page used by 5000 users.")), RESUME);
    assertEquals(1, out.size());
    assertNull(out.get(0).rewrite());
  }

  @Test
  void keepsRewriteUsingNumbersFromResume() {
    List<Issue> out = v.validate(List.of(ai("Worked on the login page",
                                            "Built a login page for 50 test users using [add tool].")), RESUME);
    assertNotNull(out.get(0).rewrite());
  }

  @Test
  void codeIssuesPassThroughWithoutEvidence() {
    Issue code = new Issue(AnalysisParameter.CONTACT_LINKS, Severity.HIGH, IssueSource.CODE,
                           null, "No GitHub link found.", "Add it.", null);
    assertEquals(1, v.validate(List.of(code), RESUME).size());
  }
}