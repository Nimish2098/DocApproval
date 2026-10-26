package com.Project.DocApproval.analysis.check;

// SkillsCheck.java
import java.util.*;
import java.util.regex.*;
import org.springframework.stereotype.Component;
import com.Project.DocApproval.analysis.dto.*;

import static com.Project.DocApproval.analysis.check.CheckSupport.issue;
import static com.Project.DocApproval.analysis.check.CheckSupport.result;

@Component
public class SkillsCheck implements ResumeCheck {
  private static final AnalysisParameter P = AnalysisParameter.SKILLS;
  private static final Pattern GROUPED_LINE = Pattern.compile("^[A-Za-z][A-Za-z &/]{1,30}:\\s*\\S.*");
  private static final List<String> FILLERS = List.of("hardworking", "hard working", "hard-working",
                                                      "team player", "quick learner", "fast learner", "self-motivated", "self motivated", "passionate",
                                                      "dedicated", "detail-oriented", "detail oriented", "good communication", "communication skills",
                                                      "punctual", "honest", "sincere", "enthusiastic");

  @Override
  public CheckResult run(ResumeDocument doc) {
    ResumeSections s = ResumeSections.parse(doc.text());
    String body = s.get(ResumeSections.Type.SKILLS);
    List<Issue> issues = new ArrayList<>();

    if (body.isBlank()) {
      issues.add(issue(P, Severity.HIGH, null, "No skills section found.",
                       "Add a Skills section grouped like: Languages, Frameworks, Tools."));
      return result(P, 0, "No skills section.", issues);
    }

    int score = 0;
    String[] lines = body.split("\\R");

    long groupedLines = Arrays.stream(lines).map(String::trim).filter(l -> GROUPED_LINE.matcher(l).matches()).count();
    if (groupedLines >= 2) {
      score += 30;
    } else {
      issues.add(issue(P, Severity.LOW, null, "Skills are not grouped.",
                       "Group them, for example: Languages: Java, Python. Frameworks: Spring Boot, React."));
    }

    Set<String> skillTerms = TechTerms.findIn(body);
    if (skillTerms.size() >= 6) score += 30;
    else if (skillTerms.size() >= 3) score += 20;
    else if (!skillTerms.isEmpty()) score += 10;
    if (skillTerms.size() < 3) {
      issues.add(issue(P, Severity.MEDIUM, null, "Very few concrete technical skills were recognised.",
                       "List the specific languages, frameworks and tools you have actually used."));
    }

    int fillerCount = 0, reported = 0;
    for (String filler : FILLERS) {
      Pattern p = Pattern.compile("(?i)\\b" + Pattern.quote(filler) + "\\b");
      for (String line : lines) {
        if (p.matcher(line).find()) {
          fillerCount++;
          if (reported++ < 3) {
            issues.add(issue(P, Severity.LOW, line.trim(),
                             "\"" + filler + "\" is a filler word that recruiters cannot verify.",
                             "Replace soft-skill words with concrete tools, and show soft skills through project bullets."));
          }
          break;
        }
      }
    }
    score += 20 - Math.min(20, 10 * fillerCount);

    if (!skillTerms.isEmpty()) {
      String rest = String.join("\n",
                                s.get(ResumeSections.Type.SUMMARY), s.get(ResumeSections.Type.EDUCATION),
                                s.get(ResumeSections.Type.EXPERIENCE), s.get(ResumeSections.Type.PROJECTS),
                                s.get(ResumeSections.Type.CERTIFICATIONS));
      Set<String> elsewhere = TechTerms.findIn(rest);
      List<String> unbacked = skillTerms.stream().filter(t -> !elsewhere.contains(t)).toList();
      double backedRatio = (double) (skillTerms.size() - unbacked.size()) / skillTerms.size();
      if (backedRatio >= 0.5) score += 20;
      else if (backedRatio > 0) score += 10;
      if (!unbacked.isEmpty()) {
        issues.add(issue(P, Severity.LOW, null,
                         "These listed skills do not appear in any project or experience: "
                         + String.join(", ", unbacked.stream().limit(5).toList()) + ".",
                         "Be ready to be asked about them in interviews, or add a project that uses them."));
      }
    }

    return result(P, score, skillTerms.size() + " recognised skills.", issues);
  }
}