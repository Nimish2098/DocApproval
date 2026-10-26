package com.Project.DocApproval.analysis.check;

// CodeCheckRunner.java
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class CodeCheckRunner {
  private final List<ResumeCheck> checks;

  public CodeCheckRunner(List<ResumeCheck> checks) { this.checks = checks; }

  public List<CheckResult> runAll(ResumeDocument doc) {
    return checks.stream().map(c -> c.run(doc)).toList();
  }
}