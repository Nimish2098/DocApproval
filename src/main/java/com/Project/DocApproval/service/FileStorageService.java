package com.Project.DocApproval.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root = Paths.get("uploads");

    public String save(MultipartFile file) throws IOException {
        // This creates a folder named 'uploads' in your project root
        Path root = Paths.get("uploads").toAbsolutePath();
        if (!Files.exists(root)) Files.createDirectories(root);

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path targetPath = root.resolve(filename);
        Files.copy(file.getInputStream(), targetPath);

        return targetPath.toString(); // Save THIS string to the database
    }
}
