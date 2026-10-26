package com.Project.DocApproval.analysis.check;

// StructureAtsCheckTest.java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StructureAtsCheckTest {
  private final StructureAtsCheck check = new StructureAtsCheck();

  @Test
  void goodResumeScoresFull() {
    CheckResult r = check.run(new ResumeDocument(SampleResumes.GOOD, 1));
    assertEquals(100, r.score().score());
    assertTrue(r.issues().isEmpty());
  }

  @Test
  void unreadableTextIsFlagged() {
    CheckResult r = check.run(new ResumeDocument("Hi", 1));
    assertEquals(10, r.score().score());
    assertEquals(1, r.issues().size());
  }

  @Test
  void threePagesPenalised() {
    assertEquals(80, check.run(new ResumeDocument(SampleResumes.GOOD, 3)).score().score());
  }

  @Test
  void twoPagesPenalisedLess() {
    assertEquals(90, check.run(new ResumeDocument(SampleResumes.GOOD, 2)).score().score());
  }

  @Test
  void unknownPageCountIsSkipped() {
    assertEquals(100, check.run(new ResumeDocument(SampleResumes.GOOD, 0)).score().score());
  }

  @Test
  void missingSectionsPenalised() {
    CheckResult r = check.run(new ResumeDocument("word ".repeat(100), 1));
    assertEquals(45, r.score().score());
  }

  @Test
  void columnLayoutDetected() {
    String cols = "Education\nSkills\nProjects\n" + "Java    Python    SQL\n".repeat(12) + "filler ".repeat(80);
    CheckResult r = check.run(new ResumeDocument(cols, 1));
    assertEquals(80, r.score().score());
    assertTrue(SampleResumes.anyIssueContains(r, "column"));
  }
}