package com.Project.DocApproval.analysis.dto;



public enum ScoreLabel {
  GETTING_STARTED("Getting started"),
  SOLID_BASE("Solid base"),
  STRONG_FOR_FRESHER("Strong for a fresher");

  private final String displayName;

  ScoreLabel(String displayName) {
    this.displayName = displayName;
  }

  public String displayName() {
    return displayName;
  }

  public static ScoreLabel fromScore(int score) {
    if (score >= 80) return STRONG_FOR_FRESHER;
    if (score >= 55) return SOLID_BASE;
    return GETTING_STARTED;
  }
}