package com.Project.DocApproval.service;

import com.Project.DocApproval.exceptions.InvalidFileFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private final Path rootLocation = Paths.get("uploads").toAbsolutePath();
    private final List<String> ALLOWED_EXTENSIONS = List.of("pdf", "docx", "txt");

    public String save(MultipartFile file, String subFolder) throws IOException {
        // 1. Validate file before doing anything else
        validateFile(file);

        Path targetFolder = this.rootLocation.resolve(subFolder);

        if (!Files.exists(targetFolder)) {
            Files.createDirectories(targetFolder);
        }

        // 2. Clean filename and add UUID to prevent overwriting
        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown";
        String filename = UUID.randomUUID() + "_" + originalFilename.replaceAll("\\s+", "_");
        Path targetPath = targetFolder.resolve(filename);

        log.info("Saving file to: {}", targetPath);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return targetPath.toString();
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileFormatException("Cannot upload an empty file.");
        }

        // Check extension
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.contains(".")) {
            throw new InvalidFileFormatException("Invalid file name.");
        }

        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidFileFormatException("Only PDF, DOCX, and TXT files are allowed.");
        }
    }
}