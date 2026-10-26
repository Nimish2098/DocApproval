package com.Project.DocApproval.analysis.check;

// ResumeSections.java
import java.util.*;

public final class ResumeSections {
  public enum Type { SUMMARY, EDUCATION, EXPERIENCE, PROJECTS, SKILLS, CERTIFICATIONS }

  private static final Map<String, Type> ALIASES = new HashMap<>();
  static {
    alias(Type.SUMMARY, "summary", "professional summary", "career objective", "objective", "profile", "about me");
    alias(Type.EDUCATION, "education", "academics", "academic background", "educational qualifications", "qualifications");
    alias(Type.EXPERIENCE, "experience", "work experience", "professional experience", "internships",
          "internship", "internship experience", "employment history", "work history");
    alias(Type.PROJECTS, "projects", "academic projects", "personal projects", "project work",
          "key projects", "technical projects");
    alias(Type.SKILLS, "skills", "technical skills", "key skills", "core competencies",
          "technologies", "tech stack", "skills and tools");
    alias(Type.CERTIFICATIONS, "certifications", "certificates", "courses", "training",
          "courses and certifications", "certifications and courses", "licenses and certifications");
  }

  private final Map<Type, String> bodies;

  private ResumeSections(Map<Type, String> bodies) { this.bodies = bodies; }

  private static void alias(Type t, String... names) {
    for (String n : names) ALIASES.put(n, t);
  }

  public static ResumeSections parse(String text) {
    Map<Type, StringBuilder> map = new EnumMap<>(Type.class);
    Type current = null;
    if (text != null) {
      for (String line : text.split("\\R")) {
        Type heading = headingOf(line);
        if (heading != null) {
          current = heading;
          map.putIfAbsent(heading, new StringBuilder());
        } else if (current != null) {
          map.get(current).append(line).append('\n');
        }
      }
    }
    Map<Type, String> bodies = new EnumMap<>(Type.class);
    map.forEach((k, v) -> bodies.put(k, v.toString()));
    return new ResumeSections(bodies);
  }

  private static Type headingOf(String line) {
    String t = line.trim();
    if (t.isEmpty() || t.length() > 40) return null;
    String n = t.toLowerCase(Locale.ROOT).replace("&", " and ")
                .replaceAll("[^a-z ]", " ").replaceAll("\\s+", " ").trim();
    return ALIASES.get(n);
  }

  public boolean has(Type t) { return bodies.containsKey(t); }
  public String get(Type t) { return bodies.getOrDefault(t, ""); }
  public Set<Type> found() { return bodies.keySet(); }
}