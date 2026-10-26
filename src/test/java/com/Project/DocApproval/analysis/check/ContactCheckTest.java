package com.Project.DocApproval.analysis.check;

// ContactCheckTest.java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContactCheckTest {
  private final ContactCheck check = new ContactCheck();

  @Test
  void goodResumeScoresFull() {
    CheckResult r = check.run(new ResumeDocument(SampleResumes.GOOD, 1));
    assertEquals(100, r.score().score());
    assertTrue(r.issues().isEmpty());
  }

  @Test
  void nothingFoundScoresZero() {
    CheckResult r = check.run(new ResumeDocument("No contact here at all", 1));
    assertEquals(0, r.score().score());
    assertEquals(4, r.issues().size());
  }

  @Test
  void wordWithoutUrlGetsHalfCredit() {
    CheckResult r = check.run(new ResumeDocument("a@b.com +91 98765 43210 LinkedIn github.com/abc", 1));
    assertEquals(90, r.score().score());
    assertTrue(SampleResumes.anyIssueContains(r, "full url"));
  }

  @Test
  void unprofessionalEmailIsFlagged() {
    CheckResult r = check.run(new ResumeDocument(
        "cool_boy@gmail.com +91 98765 43210 linkedin.com/in/x github.com/x", 1));
    assertEquals(100, r.score().score());
    assertEquals(1, r.issues().size());
    assertEquals("cool_boy@gmail.com", r.issues().get(0).evidence());
  }
}