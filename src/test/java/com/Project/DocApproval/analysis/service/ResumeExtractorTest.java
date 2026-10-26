package com.Project.DocApproval.analysis.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResumeTextExtractorTest {
  @Test
  void keepsLineBreaksButCollapsesSpaces() {
    String out = ResumeTextExtractor.cleanKeepingLines("Skills  \r\n  Java,   Python\n\n\n\nProjects");
    assertEquals("Skills\nJava, Python\n\nProjects", out);
  }
}