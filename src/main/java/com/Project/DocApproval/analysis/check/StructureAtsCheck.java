package com.Project.DocApproval.analysis.check;

// StructureAtsCheck.java
import java.util.*;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import com.Project.DocApproval.analysis.dto.*;

import static com.Project.DocApproval.analysis.check.CheckSupport.issue;
import static com.Project.DocApproval.analysis.check.CheckSupport.result;

@Component
public class StructureAtsCheck implements ResumeCheck {
  private static final AnalysisParameter P = AnalysisParameter.STRUCTURE_ATS;
  private static final Pattern GAP = Pattern.compile("\\S(?:\\t|\\s{3,})\\S");

  @Override
  public CheckResult run(ResumeDocument doc) {
    String text = doc.text() == null ? "" : doc.text();
    List<Issue> issues = new ArrayList<>();
    int words = TextUtils.wordCount(text);

    if (words < 80) {
      issues.add(issue(P, Severity.HIGH, null,
                       "Almost no readable text was found. The file may be a scanned image or use text inside images.",
                       "Export your resume as a text-based PDF or DOCX from Word or Google Docs instead of scanning it."));
      return result(P, 10, "Resume text could not be read.", issues);
    }

    int score = 100;
    ResumeSections s = ResumeSections.parse(text);

    if (!s.has(ResumeSections.Type.EDUCATION)) {
      score -= 15;
      issues.add(issue(P, Severity.MEDIUM, null, "No Education heading found.",
                       "Add a section titled exactly \"Education\" with your degree, college and years."));
    }
    if (!s.has(ResumeSections.Type.SKILLS)) {
      score -= 15;
      issues.add(issue(P, Severity.MEDIUM, null, "No Skills heading found.",
                       "Add a section titled exactly \"Skills\" listing your technologies."));
    }
    if (!s.has(ResumeSections.Type.PROJECTS) && !s.has(ResumeSections.Type.EXPERIENCE)) {
      score -= 25;
      issues.add(issue(P, Severity.HIGH, null, "No Projects or Experience heading found.",
                       "Add a \"Projects\" section. Projects are your main proof of skill at this stage."));
    }

    int pages = doc.pageCount();
    if (pages > 2) {
      score -= 20;
      issues.add(issue(P, Severity.HIGH, null, "The resume is " + pages + " pages long.",
                       "Cut it down to one page. Recruiters skim freshers' resumes in seconds."));
    } else if (pages == 2) {
      score -= 10;
      issues.add(issue(P, Severity.MEDIUM, null, "The resume is 2 pages long.",
                       "A fresher resume works best on one page. Keep only your strongest points."));
    }

    if (looksLikeColumns(text)) {
      score -= 20;
      issues.add(issue(P, Severity.MEDIUM, null,
                       "The layout looks like multiple columns or tables, which many ATS tools read in the wrong order.",
                       "Use a single-column layout with plain headings and bullet points."));
    }

    if (words > 900) {
      score -= 10;
      issues.add(issue(P, Severity.LOW, null, "The resume has " + words + " words, which is too long.",
                       "Aim for 300 to 600 words."));
    }

    String summary = score >= 85 ? "Clean, ATS-friendly structure." : "Some structure problems may hurt ATS parsing.";
    return result(P, score, summary, issues);
  }

  static boolean looksLikeColumns(String text) {
    int nonEmpty = 0, gap = 0;
    for (String line : text.split("\\R")) {
      if (line.isBlank()) continue;
      nonEmpty++;
      if (GAP.matcher(line.trim()).find()) gap++;
    }
    return nonEmpty >= 8 && (double) gap / nonEmpty > 0.25;
  }
}
