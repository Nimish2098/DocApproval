package com.Project.DocApproval.analysis.ai;


// ResumeAiAnalyzer.java
import java.util.*;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import com.Project.DocApproval.analysis.dto.*;

@Component
public class ResumeAiAnalyzer {
  private static final Logger log = LoggerFactory.getLogger(ResumeAiAnalyzer.class);
  private static final int MAX_RESUME_CHARS = 12_000;
  private static final int MAX_ATTEMPTS = 2; // first try + one retry
  static final Set<AnalysisParameter> AI_PARAMETERS = EnumSet.of(AnalysisParameter.PROJECTS,
                                                                 AnalysisParameter.SKILLS, AnalysisParameter.WRITING_QUALITY, AnalysisParameter.HIDDEN_MATERIAL);

  private final ChatClient chatClient;

  public ResumeAiAnalyzer(ChatClient.Builder builder) {
    this.chatClient = builder.defaultSystem(ResumePrompts.SYSTEM).build();
  }

  public AiAssessment analyze(String resumeText) {
    String text = resumeText == null ? "" : resumeText;
    String resume = text.length() > MAX_RESUME_CHARS ? text.substring(0, MAX_RESUME_CHARS) : text;

    RuntimeException last = null;
    for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
      try {
        AiAssessment raw = chatClient.prompt()
                                     .user(u -> u.text(ResumePrompts.USER).param("resume", resume))
                                     .call()
                                     .entity(AiAssessment.class);
        return sanitize(raw);
      } catch (RuntimeException ex) {
        last = ex;
        log.warn("AI analysis attempt {} of {} failed: {}", attempt, MAX_ATTEMPTS, ex.getMessage());
      }
    }
    throw new IllegalStateException("AI analysis failed after " + MAX_ATTEMPTS + " attempts.", last);
  }

  /** Cleans the AI output. Throws (which triggers the retry) only when the response is unusable. */
  static AiAssessment sanitize(AiAssessment raw) {
    if (raw == null) throw new IllegalStateException("AI returned an empty response.");

    Map<AnalysisParameter, ParameterScore> scores = new EnumMap<>(AnalysisParameter.class);
    for (ParameterScore s : raw.scores()) {
      if (s == null || s.parameter() == null || !AI_PARAMETERS.contains(s.parameter())) continue;
      int clamped = Math.max(0, Math.min(100, s.score()));
      scores.putIfAbsent(s.parameter(),
                         new ParameterScore(s.parameter(), clamped, s.parameter().weight(), s.summary()));
    }
    if (!scores.keySet().containsAll(AI_PARAMETERS)) {
      throw new IllegalStateException("AI response is missing scores for some parameters.");
    }

    List<Issue> issues = raw.issues().stream()
                            .filter(i -> i != null && i.parameter() != null && AI_PARAMETERS.contains(i.parameter())
                                         && i.severity() != null && notBlank(i.evidence()) && notBlank(i.problem()) && notBlank(i.fix()))
                            .limit(8)
                            .map(i -> new Issue(i.parameter(), i.severity(), IssueSource.AI, i.evidence().trim(),
                                                i.problem().trim(), i.fix().trim(), notBlank(i.rewrite()) ? i.rewrite().trim() : null))
                            .toList();

    List<MissingMaterial> missing = raw.missingMaterial().stream()
                                       .filter(m -> m != null && notBlank(m.type()) && notBlank(m.howToAdd()))
                                       .limit(5).toList();

    List<ActionPlanStep> sorted = raw.actionPlan().stream()
                                     .filter(a -> a != null && notBlank(a.title()))
                                     .sorted(Comparator.comparingInt(ActionPlanStep::priority))
                                     .limit(3).toList();
    if (sorted.isEmpty()) throw new IllegalStateException("AI response has no action plan.");
    List<ActionPlanStep> plan = IntStream.range(0, sorted.size())
                                         .mapToObj(i -> {
                                           ActionPlanStep a = sorted.get(i);
                                           return new ActionPlanStep(i + 1, a.title(), a.details(), a.timeEstimate(),
                                                                     a.impact() == null ? Impact.MEDIUM : a.impact());
                                         }).toList();

    return new AiAssessment(raw.summary() == null ? "" : raw.summary().trim(),
                            new ArrayList<>(scores.values()), issues, missing, plan);
  }

  private static boolean notBlank(String s) { return s != null && !s.isBlank(); }
}
