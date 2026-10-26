package com.Project.DocApproval.analysis.check;

// ContactCheck.java
import java.util.*;
import java.util.regex.*;

import org.springframework.stereotype.Component;
import com.Project.DocApproval.analysis.dto.*;

import static com.Project.DocApproval.analysis.check.CheckSupport.issue;
import static com.Project.DocApproval.analysis.check.CheckSupport.result;

@Component
public class ContactCheck implements ResumeCheck {
  private static final AnalysisParameter P = AnalysisParameter.CONTACT_LINKS;
  private static final Pattern EMAIL = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
  private static final Pattern PHONE = Pattern.compile(
      "(?<!\\d)(?:\\+?91[\\s-]?)?[6-9]\\d{4}[\\s-]?\\d{5}(?!\\d)|(?<!\\d)\\+\\d{1,3}[\\s-]?\\d{6,12}(?!\\d)");
  private static final Pattern LINKEDIN_URL = Pattern.compile("(?i)linkedin\\.com/in/[\\w%-]+");
  private static final Pattern LINKEDIN_WORD = Pattern.compile("(?i)\\blinkedin\\b");
  private static final Pattern GITHUB_URL = Pattern.compile("(?i)github\\.com/[\\w-]+");
  private static final Pattern GITHUB_WORD = Pattern.compile("(?i)\\bgithub\\b");
  private static final Pattern UNPROFESSIONAL =
      Pattern.compile("(?i)(cool|sexy|hot|lover|baby|dude|killer|sweet|angel)");

  @Override
  public CheckResult run(ResumeDocument doc) {
    String text = doc.text() == null ? "" : doc.text();
    List<Issue> issues = new ArrayList<>();
    int score = 0, found = 0;

    Matcher email = EMAIL.matcher(text);
    if (email.find()) {
      score += 35; found++;
      String address = email.group();
      String local = address.substring(0, address.indexOf('@'));
      if (UNPROFESSIONAL.matcher(local).find()) {
        issues.add(issue(P, Severity.LOW, address, "This email address may look unprofessional.",
                         "Create a simple email like firstname.lastname@gmail.com for job applications."));
      }
    } else {
      issues.add(issue(P, Severity.HIGH, null, "No email address found.", "Add a professional email in the header."));
    }

    if (PHONE.matcher(text).find()) {
      score += 25; found++;
    } else {
      issues.add(issue(P, Severity.HIGH, null, "No phone number found.", "Add a phone number in the header."));
    }

    if (LINKEDIN_URL.matcher(text).find()) {
      score += 20; found++;
    } else if (LINKEDIN_WORD.matcher(text).find()) {
      score += 10;
      issues.add(issue(P, Severity.LOW, null,
                       "LinkedIn is mentioned but the full URL is not visible in the text.",
                       "Write the full URL (linkedin.com/in/yourname) as visible text, not only a hidden hyperlink."));
    } else {
      issues.add(issue(P, Severity.MEDIUM, null, "No LinkedIn profile link found.",
                       "Add your LinkedIn URL. Recruiters check it."));
    }

    if (GITHUB_URL.matcher(text).find()) {
      score += 20; found++;
    } else if (GITHUB_WORD.matcher(text).find()) {
      score += 10;
      issues.add(issue(P, Severity.LOW, null,
                       "GitHub is mentioned but the full URL is not visible in the text.",
                       "Write the full URL (github.com/yourname) as visible text, not only a hidden hyperlink."));
    } else {
      issues.add(issue(P, Severity.HIGH, null, "No GitHub link found.",
                       "Add your GitHub profile link. It is your main proof of skill without work experience."));
    }

    return result(P, score, found + " of 4 contact items fully present.", issues);
  }
}