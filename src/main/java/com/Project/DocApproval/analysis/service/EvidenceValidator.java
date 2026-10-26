package com.Project.DocApproval.analysis.service;

// EvidenceValidator.java
import java.util.*;
import java.util.regex.*;
import org.springframework.stereotype.Component;
import com.Project.DocApproval.analysis.dto.*;

@Component
public class EvidenceValidator {
  private static final Pattern NUMBER = Pattern.compile("\\d+(?:[.,]\\d+)*");
  private static final int MIN_EVIDENCE_CHARS = 8;

  public List<Issue> validate(List<Issue> issues, String resumeText) {
    String haystack = " " + normalize(resumeText) + " ";
    List<Issue> out = new ArrayList<>();
    for (Issue i : issues) {
      if (i.source() != IssueSource.AI) {
        out.add(i);
        continue;
      }
      String ev = normalize(i.evidence());
      if (ev.length() < MIN_EVIDENCE_CHARS || !haystack.contains(" " + ev + " ")) continue;
      out.add(hasInventedNumber(i.rewrite(), haystack) ? withoutRewrite(i) : i);
    }
    return out;
  }

  private boolean hasInventedNumber(String rewrite, String haystack) {
    if (rewrite == null) return false;
    Matcher m = NUMBER.matcher(rewrite);
    while (m.find()) {
      if (!haystack.contains(" " + normalize(m.group()) + " ")) return true;
    }
    return false;
  }

  private Issue withoutRewrite(Issue i) {
    return new Issue(i.parameter(), i.severity(), i.source(), i.evidence(), i.problem(), i.fix(), null);
  }

  static String normalize(String s) {
    if (s == null) return "";
    return s.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]+", " ").trim();
  }
}