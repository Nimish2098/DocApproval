package com.Project.DocApproval.analysis.check;
// WritingCheck.java
import java.util.*;
import java.util.regex.*;

import org.springframework.stereotype.Component;
import com.Project.DocApproval.analysis.dto.*;

import static com.Project.DocApproval.analysis.check.CheckSupport.issue;
import static com.Project.DocApproval.analysis.check.CheckSupport.result;

@Component
public class WritingCheck implements ResumeCheck {
  private static final AnalysisParameter P = AnalysisParameter.WRITING_QUALITY;
  private static final Set<String> ACTION_VERBS = new HashSet<>(List.of(
      "built", "developed", "designed", "implemented", "created", "led", "managed", "optimized",
      "integrated", "deployed", "automated", "analyzed", "tested", "collaborated", "improved", "reduced",
      "increased", "launched", "wrote", "configured", "migrated", "refactored", "debugged", "engineered",
      "architected", "delivered", "maintained", "contributed", "organized", "coordinated", "presented",
      "researched", "trained", "resolved", "streamlined", "achieved", "authored", "constructed",
      "customized", "documented", "enhanced", "established", "executed", "generated", "initiated",
      "modeled", "monitored", "prototyped", "redesigned", "secured", "simplified", "solved", "supported",
      "tracked", "validated", "visualized", "scraped", "applied", "extracted", "processed", "added",
      "fixed", "connected", "converted", "compared", "evaluated", "explored", "published", "mentored",
      "volunteered", "taught", "won", "ranked"));
  private static final List<String> WEAK_STARTS = List.of("responsible for", "worked on", "worked with",
                                                          "helped", "involved in", "assisted", "duties", "participated in", "was part of", "tasked with");
  private static final Pattern FIRST_WORD = Pattern.compile("[a-z]+");
  private static final Pattern DIGIT = Pattern.compile("\\d");
  private static final Pattern FIRST_PERSON = Pattern.compile(
      "(?i)^(i|my)\\s|\\bI\\s(am|have|was|worked|developed|built|did|made)\\b");

  @Override
  public CheckResult run(ResumeDocument doc) {
    String text = doc.text() == null ? "" : doc.text();
    ResumeSections s = ResumeSections.parse(text);
    String scope = s.get(ResumeSections.Type.EXPERIENCE) + "\n" + s.get(ResumeSections.Type.PROJECTS);
    List<String> bullets = TextUtils.bullets(scope.isBlank() ? text : scope);
    List<Issue> issues = new ArrayList<>();

    if (bullets.isEmpty()) {
      issues.add(issue(P, Severity.HIGH, null, "No bullet points found.",
                       "Describe each project in short bullets that start with an action verb."));
      return result(P, 0, "No bullet points found.", issues);
    }

    int total = bullets.size(), action = 0, withNumbers = 0, goodLength = 0, firstPerson = 0;
    int weakReported = 0, longReported = 0;
    String firstOther = null;

    for (String b : bullets) {
      String lower = b.toLowerCase(Locale.ROOT);
      boolean weak = WEAK_STARTS.stream().anyMatch(lower::startsWith);
      if (weak) {
        if (weakReported++ < 3) {
          issues.add(issue(P, Severity.MEDIUM, b, "Starts with a weak phrase instead of an action verb.",
                           "Start with a verb like Built, Developed or Implemented, then say what you did."));
        }
      } else {
        Matcher m = FIRST_WORD.matcher(lower);
        if (m.find() && ACTION_VERBS.contains(m.group())) action++;
        else if (firstOther == null) firstOther = b;
      }

      if (DIGIT.matcher(b).find()) withNumbers++;

      int w = TextUtils.wordCount(b);
      if (w >= 6 && w <= 30) goodLength++;
      else if (w > 30 && longReported++ < 2) {
        issues.add(issue(P, Severity.LOW, b, "This bullet is too long.",
                         "Split it or cut it to one clear line of about 15 to 25 words."));
      }

      if (FIRST_PERSON.matcher(b).find() && firstPerson++ == 0) {
        issues.add(issue(P, Severity.LOW, b, "Written in first person (I / my).",
                         "Drop the pronoun and start with a verb, for example: Built a login system using JWT."));
      }
    }

    double actionRatio = (double) action / total;
    int score = (int) Math.round(40 * actionRatio);
    if (actionRatio < 0.5 && firstOther != null) {
      issues.add(issue(P, Severity.MEDIUM, firstOther, "This bullet does not start with an action verb.",
                       "Begin bullets with a verb such as Built, Designed or Implemented."));
    }

    double numberRatio = (double) withNumbers / total;
    score += numberRatio >= 0.25 ? 20 : (withNumbers > 0 ? 10 : 0);
    if (withNumbers == 0) {
      issues.add(issue(P, Severity.LOW, null, "No numbers appear in your bullets.",
                       "Add numbers only where true: users, records, pages, test cases, response time, percent improvement."));
    }

    score += (int) Math.round(20.0 * goodLength / total);
    score += firstPerson == 0 ? 20 : (firstPerson == 1 ? 10 : 0);

    return result(P, score, action + " of " + total + " bullets start with an action verb.", issues);
  }
}