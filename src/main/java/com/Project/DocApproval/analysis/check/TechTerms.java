package com.Project.DocApproval.analysis.check;

// TechTerms.java  (software-focused vocabulary; extend it for other fields)
import java.util.*;
import java.util.regex.Pattern;

final class TechTerms {
  private static final Map<String, Pattern> PATTERNS = new LinkedHashMap<>();
  static {
    for (String t : List.of("java", "python", "javascript", "typescript", "c++", "c#", "golang", "rust",
                            "kotlin", "swift", "php", "ruby", "sql", "mysql", "postgresql", "postgres", "mongodb", "redis",
                            "sqlite", "oracle", "html", "css", "react", "angular", "vue", "next.js", "node.js", "nodejs",
                            "spring", "hibernate", "django", "flask", "fastapi", "tailwind", "bootstrap", "docker",
                            "kubernetes", "git", "github", "aws", "azure", "gcp", "linux", "firebase", "graphql", "jwt",
                            "junit", "maven", "gradle", "jenkins", "figma", "postman", "pandas", "numpy", "tensorflow",
                            "pytorch", "scikit-learn", "opencv", "android", "flutter", "kafka", "rabbitmq", "selenium",
                            "tableau", "power bi", "excel", "machine learning", "data structures", "algorithms")) {
      PATTERNS.put(t, Pattern.compile("(?<![\\w+#.])" + Pattern.quote(t) + "(?![\\w+#])",
                                      Pattern.CASE_INSENSITIVE));
    }
  }

  private TechTerms() {}

  static Set<String> findIn(String text) {
    Set<String> found = new LinkedHashSet<>();
    if (text == null || text.isBlank()) return found;
    PATTERNS.forEach((term, p) -> { if (p.matcher(text).find()) found.add(term); });
    return found;
  }
}