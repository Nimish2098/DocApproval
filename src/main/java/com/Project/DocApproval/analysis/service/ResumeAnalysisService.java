package com.Project.DocApproval.analysis.service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.Project.DocApproval.analysis.ai.*;
import com.Project.DocApproval.analysis.check.*;
import com.Project.DocApproval.analysis.dto.*;
import com.Project.DocApproval.analysis.entity.ResumeAnalysisEntity;
import com.Project.DocApproval.analysis.exception.*;
import com.Project.DocApproval.analysis.repository.ResumeAnalysisRepository;

@Service
public class ResumeAnalysisService {

  private static final Logger log =
      LoggerFactory.getLogger(ResumeAnalysisService.class);

  private static final long MAX_BYTES = 2L * 1024 * 1024;
  private static final int MIN_WORDS = 80;

  private final ResumeTextExtractor textExtractor;
  private final CodeCheckRunner checkRunner;
  private final ResumeAiAnalyzer aiAnalyzer;
  private final ReportAssembler assembler;
  private final ResumeAnalysisRepository repository;

  public ResumeAnalysisService(
      ResumeTextExtractor textExtractor,
      CodeCheckRunner checkRunner,
      ResumeAiAnalyzer aiAnalyzer,
      ReportAssembler assembler,
      ResumeAnalysisRepository repository) {

    this.textExtractor = textExtractor;
    this.checkRunner = checkRunner;
    this.aiAnalyzer = aiAnalyzer;
    this.assembler = assembler;
    this.repository = repository;
  }
  private static long ms(long startNanos) {
    return (System.nanoTime() - startNanos) / 1_000_000;
  }

  // A null userId means the request is from a guest.
  // The slow AI call is intentionally outside a database transaction.
  public ResumeAnalysisResponse analyze(
      MultipartFile file, Long userId) throws IOException {

    long start = System.nanoTime();                       // 1. FIRST line of the method

    validateFile(file);

    ResumeTextExtractor.ExtractedResume extracted =
        textExtractor.extract(file);

    String text = extracted.text() == null ? "" : extracted.text();

    log.info("extraction done at {} ms", ms(start));

    if (wordCount(text) < MIN_WORDS) {
      throw new InvalidRequestException(
          "We couldn't find readable text in this file. "
          + "If it is a scan or image, export your resume as "
          + "a text-based PDF or DOCX and try again.");
    }

    ResumeDocument doc =
        new ResumeDocument(text, extracted.pageCount());

    List<CheckResult> checks = checkRunner.runAll(doc);

    AiAssessment ai;
    try {
      ai = aiAnalyzer.analyze(text);
    } catch (RuntimeException ex) {
      log.error("AI analysis failed", ex);
      throw new AnalysisUnavailableException(
          "AI analysis failed", ex);
    }
    log.info("AI ready at {} ms", ms(start));             // 4. right after the AI call

    ResumeReport report = assembler.assemble(checks, ai, text);
    int overall = assembler.overall(report);

    String filename = safeFilename(extracted.filename());
    ScoreLabel scoreLabel = ScoreLabel.fromScore(overall);

    // Guests receive the analysis result but nothing is persisted.
    if (userId == null) {
      return new ResumeAnalysisResponse(
          null,
          filename,
          extracted.contentType(),
          overall,
          scoreLabel,
          report,
          Instant.now());
    }

    // Registered users' reports are saved against their account.
    ResumeAnalysisEntity saved = repository.save(
        new ResumeAnalysisEntity(
            userId,
            filename,
            extracted.contentType(),
            overall,
            scoreLabel,
            report));
    log.info("total {} ms", ms(start));                   // 5. right after saving

    return toResponse(saved);
  }

  @Transactional(readOnly = true)
  public List<ResumeAnalysisResponse> history(Long userId) {
    return repository
        .findAllByUserIdOrderByCreatedAtDesc(userId)
        .stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public ResumeAnalysisResponse getById(Long id, Long userId) {
    return repository.findByIdAndUserId(id, userId)
                     .map(this::toResponse)
                     .orElseThrow(() ->
                                      new ResumeAnalysisNotFoundException(id));
  }

  @Transactional
  public void delete(Long id, Long userId) {
    ResumeAnalysisEntity entity =
        repository.findByIdAndUserId(id, userId)
                  .orElseThrow(() ->
                                   new ResumeAnalysisNotFoundException(id));

    repository.delete(entity);
  }

  private void validateFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new InvalidRequestException("Please choose a file.");
    }

    if (file.getSize() > MAX_BYTES) {
      throw new InvalidRequestException(
          "Please upload a file under 2 MB.");
    }

    String name = file.getOriginalFilename() == null
                  ? ""
                  : file.getOriginalFilename().toLowerCase(Locale.ROOT);

    if (!(name.endsWith(".pdf") || name.endsWith(".docx"))) {
      throw new InvalidRequestException(
          "Only PDF and DOCX files are supported.");
    }
  }

  private int wordCount(String text) {
    String trimmed = text.trim();
    return trimmed.isEmpty()
           ? 0
           : trimmed.split("\\s+").length;
  }

  private String safeFilename(String filename) {
    String name = filename == null
                  ? "resume"
                  : filename.replace('\\', '/');

    name = name.substring(name.lastIndexOf('/') + 1)
               .replaceAll("[\\r\\n]", "_");

    return name.length() > 255
           ? name.substring(name.length() - 255)
           : name;
  }

  private ResumeAnalysisResponse toResponse(
      ResumeAnalysisEntity entity) {

    return new ResumeAnalysisResponse(
        entity.getId(),
        entity.getOriginalFilename(),
        entity.getContentType(),
        entity.getOverallScore(),
        entity.getScoreLabel(),
        entity.getReport(),
        entity.getCreatedAt());
  }
}