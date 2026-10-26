package com.Project.DocApproval.analysis.check;

// ProjectsCheckTest.java
import com.Project.DocApproval.analysis.dto.Severity;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProjectsCheckTest {
  private final ProjectsCheck check = new ProjectsCheck();

  @Test
  void goodResumeScoresFull() {
    assertEquals(100, check.run(new ResumeDocument(SampleResumes.GOOD, 1)).score().score());
  }

  @Test
  void missingSectionScoresZero() {
    CheckResult r = check.run(new ResumeDocument("Skills\nJava", 1));
    assertEquals(0, r.score().score());
    assertEquals(Severity.HIGH, r.issues().get(0).severity());
  }

  @Test
  void thinProjectGetsLowScoreAndIssues() {
    String text = "Projects\nLibrary System\nJava and MySQL application for books.\n";
    CheckResult r = check.run(new ResumeDocument(text, 1));
    assertEquals(33, r.score().score());
    assertTrue(SampleResumes.anyIssueContains(r, "link"));
    assertTrue(SampleResumes.anyIssueContains(r, "bullet"));
  }

  @Test
  void techStackLineIsNotCountedAsProject() {
    String text = """
                Projects
                Chat App
                Tech Stack: Java, Spring Boot
                • Built a chat server with WebSockets.
                • Added message history using MySQL.
                """;
    assertEquals(60, check.run(new ResumeDocument(text, 1)).score().score());
  }
}