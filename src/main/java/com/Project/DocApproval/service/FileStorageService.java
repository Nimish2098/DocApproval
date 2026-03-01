package com.Project.DocApproval.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    // The base directory for all uploads
    private final Path rootLocation = Paths.get("uploads").toAbsolutePath();

    public String save(MultipartFile file, String subFolder) throws IOException {
        // 1. Define the specific path: uploads/resumes or uploads/jds
        Path targetFolder = this.rootLocation.resolve(subFolder);

        // 2. Create the directories if they don't exist
        if (!Files.exists(targetFolder)) {
            Files.createDirectories(targetFolder);
        }

        // 3. Create a unique filename
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path targetPath = targetFolder.resolve(filename);

        // 4. Save the file (using REPLACE_EXISTING for safety)
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // 5. Return the full path as a string for the database
        return targetPath.toString();
    }
}