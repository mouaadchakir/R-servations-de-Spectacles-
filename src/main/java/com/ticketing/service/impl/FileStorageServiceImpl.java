package com.ticketing.service.impl;

import com.ticketing.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path rootLocation;

    public FileStorageServiceImpl() {
        this.rootLocation = Paths.get("src/main/resources/static/uploads");
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String directoryName) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file");
        }
        
        // Create the target directory if it doesn't exist
        Path targetDir = this.rootLocation.resolve(directoryName);
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }
        
        // Generate a unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString() + extension;
        
        // Copy the file to the target location
        Path targetLocation = targetDir.resolve(newFilename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        // Return the relative path to be stored in the database
        return "/uploads/" + directoryName + "/" + newFilename;
    }

    @Override
    public boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        // Convert the relative path to an absolute path
        // The filePath is expected to start with "/uploads/"
        String relativePath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        Path targetFile = Paths.get("src/main/resources/static").resolve(relativePath);
        
        try {
            return Files.deleteIfExists(targetFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }

    @Override
    public Path getFilePath(String fileName, String directoryName) {
        return this.rootLocation.resolve(directoryName).resolve(fileName);
    }
}
