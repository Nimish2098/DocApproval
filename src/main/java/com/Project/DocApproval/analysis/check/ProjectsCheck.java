package com.Project.DocApproval.analysis.check;

// ProjectsCheck.java
import java.util.*;
import java.util.regex.*;

import com.Project.DocApproval.analysis.dto.*;

import static com.Project.DocApproval.analysis.check.CheckSupport.issue;
import static com.Project.DocApproval.analysis.check.CheckSupport.result;
import org.springframework.stereotype.Component;

@Component
public class ProjectsCheck implements ResumeCheck {
  private static final AnalysisParameter P = AnalysisParameter.PROJECTS;
  private static final Pattern LINK = Pattern.compile(
      "(?i)(https?://\\S+|github\\.com/\\S+|www\\.\\S+|\\S+\\.(?:vercel\\.app|netlify\\.app|onrender\\.com|herokuapp\\.com)\\S*)");
  private static final Pattern NOT_A_TITLE = Pattern.compile(
      "(?i)^(tech(nologies)?( stack)?|tools?|stack|built with|link|github|demo|live)\\s*:.*");

  @Override
  public CheckResult run(ResumeDocument doc) {
    ResumeSections sections = ResumeSections.parse(doc.text());
    String body = sections.get(ResumeSections.Type.PROJECTS);
    List<Issue> issues = new ArrayList<>();

    if (body.isBlank()) {
      issues.add(issue(P, Severity.HIGH, null, "No projects found.",
                       "Add 2 or 3 projects: college mini-projects count. Projects show skill when you have no job history."));
      return result(P, 0, "No projects section.", issues);
    }

    int count = Math.max(1, projectTitles(body).size());
    List<String> bullets = TextUtils.bullets(body);
    int score = count >= 2 ? 40 : 25;
    if (count < 2) {
      issues.add(issue(P, Severity.MEDIUM, null, "Only one project found.",
                       "Add a second project. Two or three is the sweet spot."));
    }

    int links = 0;
    Matcher m = LINK.matcher(body);
    while (m.find()) links++;
    if (links >= Math.min(count, 2)) {
      score += 25;
    } else if (links > 0) {
      score += 12;
      issues.add(issue(P, Severity.MEDIUM, null, "Some projects have no link.",
                       "Add a GitHub or live demo link to every project."));
    } else {
      issues.add(issue(P, Severity.HIGH, null, "No project links found.",
                       "Add a GitHub or live demo link to every project, written as visible text."));
    }

    int techTerms = TechTerms.findIn(body).size();
    if (techTerms >= 3) {
      score += 15;
    } else {
      if (techTerms >= 1) score += 8;
      issues.add(issue(P, Severity.MEDIUM, null, "Few technologies are named in your projects.",
                       "Name the tech stack for each project, for example: Java, Spring Boot, MySQL."));
    }

    if (bullets.size() >= 2 * count) {
      score += 20;
    } else {
      if (bullets.size() >= count) score += 10;
      issues.add(issue(P, Severity.MEDIUM, null, "Projects have too few bullet points.",
                       "Write 2 or 3 bullets per project: what you built, with which tools, and what it does."));
    }

    return result(P, score, count + " project(s), " + links + " link(s), " + bullets.size() + " bullet(s).", issues);
  }

  /** Heuristic: short, capitalised, non-bullet lines that are not continuation text or "Tech Stack:" lines. */
  static List<String> projectTitles(String body) {
    List<String> titles = new ArrayList<>();
    for (String raw : body.split("\\R")) {
      String line = raw.trim();
      if (line.isEmpty() || TextUtils.isBullet(line) || NOT_A_TITLE.matcher(line).matches()) continue;
      if (TextUtils.wordCount(line) > 14) continue;
      char first = line.charAt(0);
      if (!(Character.isUpperCase(first) || Character.isDigit(first))) continue;
      if (line.endsWith(".") || line.endsWith(",")) continue;
      titles.add(line);
    }
    return titles;
  }
}