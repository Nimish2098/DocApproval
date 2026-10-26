package com.Project.DocApproval.analysis.service;

import com.Project.DocApproval.analysis.exception.InvalidRequestException;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

@Service
public class ResumeTextExtractor {

  public record ExtractedResume(String text, String filename, String contentType, int pageCount) {}

  private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
  private static final int MIN_TEXT_LENGTH = 50;

  private static final String PDF = "application/pdf";
  private static final String DOCX =
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

  private final Tika tika = new Tika();

  public ExtractedResume extract(MultipartFile file) throws IOException {
    validateFile(file);

    String filename = file.getOriginalFilename();
    if (filename == null || filename.isBlank()) {
      throw new InvalidRequestException("Filename is required.");
    }

    String lowerFilename = filename.toLowerCase(Locale.ROOT);
    boolean isPdf = lowerFilename.endsWith(".pdf");
    boolean isDocx = lowerFilename.endsWith(".docx");
    if (!isPdf && !isDocx) {
      throw new InvalidRequestException("Only PDF and DOCX files are supported.");
    }

    // Verify the actual file content, not just its extension.
    String detectedType;
    try (InputStream input = file.getInputStream()) {
      detectedType = tika.detect(input, filename);
    }
    boolean validType = isPdf ? PDF.equals(detectedType) : DOCX.equals(detectedType);
    if (!validType) {
      throw new InvalidRequestException(
          "File content does not match its extension, or the document is invalid.");
    }

    Metadata metadata = new Metadata();
    String raw;
    try (InputStream input = file.getInputStream()) {
      raw = tika.parseToString(input, metadata);
    } catch (TikaException e) {
      throw new InvalidRequestException(
          "Unable to read the resume. Please upload a valid PDF or DOCX file.");
    }

    String text = cleanKeepingLines(raw);
    if (text.length() < MIN_TEXT_LENGTH) {
      throw new InvalidRequestException(
          "Could not extract enough text from the resume. The file may be scanned or empty.");
    }

    // Page count is reliable for PDFs only. DOCX page metadata is often stale, so 0 = unknown.
    int pageCount = isPdf ? readPageCount(metadata) : 0;
    return new ExtractedResume(text, filename, detectedType, pageCount);
  }

  /** Cleans spaces inside each line but keeps the line breaks the checks rely on. */
  static String cleanKeepingLines(String raw) {
    if (raw == null) return "";
    return raw.replace("\r\n", "\n").replace('\r', '\n')
              .replace('\u00A0', ' ')
              .replaceAll("[ \\t\\x0B\\f]+", " ")   // collapse spaces/tabs, NOT newlines
              .replaceAll(" ?\\n ?", "\n")          // trim around line breaks
              .replaceAll("\\n{3,}", "\n\n")        // at most one blank line in a row
              .trim();
  }

  private int readPageCount(Metadata metadata) {
    try {
      String pages = metadata.get("xmpTPg:NPages");
      if (pages != null) return Integer.parseInt(pages.trim());
    } catch (NumberFormatException ignored) { }
    return 0;
  }

  private void validateFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new InvalidRequestException("Please upload a resume.");
    }
    if (file.getSize() > MAX_FILE_SIZE) {
      throw new InvalidRequestException("Resume file size must not exceed 2 MB.");
    }
  }
}