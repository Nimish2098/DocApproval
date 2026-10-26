package com.Project.DocApproval.analysis.service;

// ScoreCalculator.java
import java.util.*;
import org.springframework.stereotype.Component;
import com.Project.DocApproval.analysis.ai.*;
import com.Project.DocApproval.analysis.check.CheckResult;
import com.Project.DocApproval.analysis.dto.*;

@Component
public class ScoreCalculator {
  private static final Map<AnalysisParameter, Double> CODE_SHARE = new EnumMap<>(AnalysisParameter.class);
  static {
    CODE_SHARE.put(AnalysisParameter.STRUCTURE_ATS, 1.0);
    CODE_SHARE.put(AnalysisParameter.CONTACT_LINKS, 1.0);
    CODE_SHARE.put(AnalysisParameter.PROJECTS, 0.4);
    CODE_SHARE.put(AnalysisParameter.SKILLS, 0.4);
    CODE_SHARE.put(AnalysisParameter.WRITING_QUALITY, 0.4);
    CODE_SHARE.put(AnalysisParameter.HIDDEN_MATERIAL, 0.0);
  }

  public List<ParameterScore> blend(List<CheckResult> checks, AiAssessment ai) {
    Map<AnalysisParameter, ParameterScore> code = new EnumMap<>(AnalysisParameter.class);
    checks.forEach(c -> {
      if (c.parameter() != null && c.score() != null) {
        code.put(c.parameter(), c.score());
      }
    });
    Map<AnalysisParameter, ParameterScore> aiScores = new EnumMap<>(AnalysisParameter.class);

    ai.scores().forEach(s -> {
      if (s != null && s.parameter() != null) {
        aiScores.put(s.parameter(), s);
      }
    });
    System.out.println("Rule-based score parameters: " + code.keySet());
    System.out.println("AI score parameters: " + aiScores.keySet());
    List<ParameterScore> out = new ArrayList<>();
    for (AnalysisParameter p : AnalysisParameter.values()) {
      if (!CODE_SHARE.containsKey(p)) {
        throw new IllegalStateException(
            "Missing CODE_SHARE configuration for " + p);
      }
      double share = CODE_SHARE.get(p);
      ParameterScore c = code.get(p);
      ParameterScore a = aiScores.get(p);
      int score;
      String summary;
      if (share >= 1.0) {
        require(c, p);
        score = c.score();
        summary = c.summary();
      } else if (share <= 0.0) {
        require(a, p);
        score = a.score();
        summary = a.summary();
      } else {
        require(c, p);
        require(a, p);
        score = (int) Math.round(share * c.score() + (1 - share) * a.score());
        summary = a.summary() + " (" + c.summary() + ")";
      }
      out.add(new ParameterScore(p, Math.max(0, Math.min(100, score)), p.weight(), summary));
    }
    return out;
  }

  public int overall(List<ParameterScore> parameters) {
    int total = parameters.stream().mapToInt(s -> s.score() * s.parameter().weight()).sum();
    return (int) Math.round(total / 100.0);
  }
  private static void require(ParameterScore score, AnalysisParameter parameter) {
    if (score == null) {
      throw new IllegalStateException(
          "Missing " + parameter + " score in the scoring input."
      );
    }
  }

}