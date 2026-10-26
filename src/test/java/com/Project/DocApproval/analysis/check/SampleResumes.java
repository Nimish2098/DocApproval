package com.Project.DocApproval.analysis.check;

// SampleResumes.java
import java.util.Locale;

final class SampleResumes {
  private SampleResumes() {}

  static final String GOOD = """
            Nimish Rao
            Bengaluru, India
            asha.verma@gmail.com | +91 98765 00000
            linkedin.com/in/asha-verma | github.com/ashaverma

            Summary
            Final-year student building web applications with Java and React.

            Education
            B.Tech in Computer Science, ABC Institute of Technology, 2022 - 2026
            CGPA: 8.1

            Skills
            Languages: Java, Python, SQL
            Frameworks: Spring Boot, React
            Tools: Git, Docker, MySQL

            Projects
            Expense Tracker | github.com/ashaverma/expense-tracker
            • Built a REST API with Spring Boot and MySQL to track daily expenses for 50 test users.
            • Developed a React dashboard that shows monthly spending charts.
            • Implemented JWT login and deployed the app with Docker.

            Resume Analyzer | https://resume-demo.vercel.app
            • Developed a Python script that extracts skills from PDF resumes.
            • Designed a scoring module and wrote unit tests with JUnit.
            • Integrated the tool with a simple web form built with React.

            Certifications
            Java Programming Masterclass, Udemy
            """;

  static boolean anyIssueContains(CheckResult r, String needle) {
    String n = needle.toLowerCase(Locale.ROOT);
    return r.issues().stream().anyMatch(i ->
                                            (i.problem() != null && i.problem().toLowerCase(Locale.ROOT).contains(n))
                                            || (i.fix() != null && i.fix().toLowerCase(Locale.ROOT).contains(n))
                                            || (i.evidence() != null && i.evidence().toLowerCase(Locale.ROOT).contains(n)));
  }
}