package com.Project.DocApproval.analysis.check;

// WritingCheckTest.java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WritingCheckTest {
  private final WritingCheck check = new WritingCheck();

  @Test
  void goodResumeScoresHigh() {
    assertTrue(check.run(new ResumeDocument(SampleResumes.GOOD, 1)).score().score() >= 85);
  }

  @Test
  void noBulletsScoresZero() {
    CheckResult r = check.run(new ResumeDocument("Projects\nLibrary System\nA system for books.", 1));
    assertEquals(0, r.score().score());
  }

  @Test
  void weakStartersAreQuoted() {
    String text = """
                Projects
                Library System
                • Responsible for building the database.
                • Worked on the login page.
                • Built a billing module with 3 reports.
                """;
    CheckResult r = check.run(new ResumeDocument(text, 1));
    assertTrue(r.score().score() < 70);
    assertTrue(r.issues().stream().anyMatch(i -> "Responsible for building the database.".equals(i.evidence())));
  }

  @Test
  void firstPersonIsFlagged() {
    String text = "Projects\nChat App\n• I worked on the backend using Java and SQL for the project.\n";
    assertTrue(SampleResumes.anyIssueContains(check.run(new ResumeDocument(text, 1)), "first person"));
  }
}