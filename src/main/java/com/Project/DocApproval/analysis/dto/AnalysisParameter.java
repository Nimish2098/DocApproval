package com.Project.DocApproval.analysis.dto;

public enum AnalysisParameter {
  STRUCTURE_ATS(20),
  CONTACT_LINKS(10),
  PROJECTS(25),
  SKILLS(15),
  WRITING_QUALITY(15),
  HIDDEN_MATERIAL(15);

  private final int weight;

  AnalysisParameter(int weight) {
    this.weight = weight;
  }

  public int weight() {
    return weight;
  }
}