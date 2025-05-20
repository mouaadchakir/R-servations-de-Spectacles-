package com.ticketing.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileStorageService {
    
    /**
     * Store a file in the file system
     * @param file The file to store
     * @param directoryName The target directory name
     * @return The path where the file was stored
     */
    String storeFile(MultipartFile file, String directoryName) throws IOException;
    
    /**
     * Delete a file from the file system
     * @param filePath The path of the file to delete
     * @return true if the file was deleted, false otherwise
     */
    boolean deleteFile(String filePath);
    
    /**
     * Get the path of a file
     * @param fileName The name of the file
     * @param directoryName The directory name
     * @return The path of the file
     */
    Path getFilePath(String fileName, String directoryName);
}
