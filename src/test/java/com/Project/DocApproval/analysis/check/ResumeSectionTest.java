package com.Project.DocApproval.analysis.check;

// ResumeSectionsTest.java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static com.Project.DocApproval.analysis.check.ResumeSections.Type.*;

class ResumeSectionTest {
  @Test
  void findsStandardSections() {
    ResumeSections s = ResumeSections.parse(SampleResumes.GOOD);
    assertTrue(s.has(EDUCATION));
    assertTrue(s.has(SKILLS));
    assertTrue(s.has(PROJECTS));
    assertTrue(s.get(SKILLS).contains("Languages: Java"));
    assertFalse(s.get(PROJECTS).contains("Java Programming Masterclass"));
  }

  @Test
  void acceptsUppercaseAndColon() {
    assertTrue(ResumeSections.parse("TECHNICAL SKILLS:\nJava, Python").has(SKILLS));
  }

  @Test
  void noHeadingsMeansNoSections() {
    assertTrue(ResumeSections.parse("Just a paragraph of text.\nAnother line.").found().isEmpty());
  }
}