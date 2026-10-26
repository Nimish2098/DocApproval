package com.Project.DocApproval.analysis.dto;

public record ParameterScore(
    AnalysisParameter parameter,
    int score,          // 0-100 for this parameter
    int weight,         // always parameter.weight(), included for the frontend
    String summary      // one sentence on why
) {
  public ParameterScore(AnalysisParameter parameter, int score, int weight, String summary) {
    this.parameter = parameter;
    this.score = score;
    this.weight = parameter == null ? weight : parameter.weight();
    this.summary = summary;
  }
}