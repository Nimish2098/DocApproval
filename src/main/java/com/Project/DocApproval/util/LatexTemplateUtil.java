package com.Project.DocApproval.util;

// util/LatexTemplateUtil.java
public class LatexTemplateUtil {

    public static String resumeTemplate(String name,
                                        String email,
                                        String phone) {
        return """
            \\documentclass[a4paper,11pt]{article}
            \\usepackage[margin=0.8in]{geometry}
            \\usepackage{enumitem}
            \\usepackage{hyperref}
            \\usepackage{titlesec}
            \\usepackage{parskip}

            \\titleformat{\\section}{\\large\\bfseries}{}{0em}{}[\\titlerule]

            \\begin{document}

            %% ── Header ──────────────────────────────────────
            \\begin{center}
                {\\Huge \\textbf{%s}} \\\\[6pt]
                \\href{mailto:%s}{%s} $\\cdot$ %s
            \\end{center}

            %% ── Summary ─────────────────────────────────────
            \\section{Summary}
            Write a brief professional summary here.

            %% ── Skills ──────────────────────────────────────
            \\section{Skills}
            \\begin{itemize}[noitemsep]
                \\item \\textbf{Languages:} Java, Python
                \\item \\textbf{Frameworks:} Spring Boot, React
                \\item \\textbf{Tools:} Docker, Git, PostgreSQL
            \\end{itemize}

            %% ── Experience ───────────────────────────────────
            \\section{Experience}
            \\textbf{Software Developer} \\hfill Jan 2024 -- Present \\\\
            Company Name, City \\\\
            \\begin{itemize}[noitemsep]
                \\item Describe what you built and its impact
                \\item Use numbers where possible
            \\end{itemize}

            %% ── Education ────────────────────────────────────
            \\section{Education}
            \\textbf{B.Tech Computer Science} \\hfill 2021 -- 2025 \\\\
            University Name, City

            %% ── Projects ─────────────────────────────────────
            \\section{Projects}
            \\textbf{ResuMatch} $|$ \\textit{Spring Boot, JWT, Docker} \\\\
            Resume analyzer with skill gap analysis and Git-inspired versioning.

            \\end{document}
            """.formatted(name, email, email, phone);
    }
}