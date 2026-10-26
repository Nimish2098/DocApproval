package com.Project.DocApproval.analysis.check;

// SkillsCheckTest.java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SkillsCheckTest {
  private final SkillsCheck check = new SkillsCheck();

  @Test
  void goodResumeScoresFull() {
    assertEquals(100, check.run(new ResumeDocument(SampleResumes.GOOD, 1)).score().score());
  }

  @Test
  void missingSectionScoresZero() {
    assertEquals(0, check.run(new ResumeDocument("Projects\nTodo App", 1)).score().score());
  }

  @Test
  void fillerWordsAreFlaggedWithEvidence() {
    CheckResult r = check.run(new ResumeDocument(
        "Skills\nHardworking, team player, quick learner, Java", 1));
    assertEquals(10, r.score().score());
    assertTrue(r.issues().stream().anyMatch(i -> i.evidence() != null && i.evidence().contains("Hardworking")));
  }

  @Test
  void unbackedSkillsAreListed() {
    String text = """
                Skills
                Languages: Java, Python
                Tools: Git, Docker, MySQL

                Projects
                Todo App
                • Built a todo app in Java.
                """;
    CheckResult r = check.run(new ResumeDocument(text, 1));
    assertEquals(80, r.score().score());
    assertTrue(SampleResumes.anyIssueContains(r, "python"));
  }
}