package com.andruf.sez.service;

import com.andruf.sez.exception.BusinessException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path root = Paths.get("uploads");

    public String save(MultipartFile file) throws IOException {
        if (!Files.exists(root)) Files.createDirectories(root);
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), this.root.resolve(filename));
        return filename;
    }
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new BusinessException("Could not read file", "FILE_READ_ERROR");
            }
        } catch (MalformedURLException e) {
            throw new BusinessException("URL is malformed: " + e.getMessage(), "FILE_READ_ERROR");
        }
    }

    public void deleteFile(String filePath) {
        try {
            Path file = root.resolve(filePath);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new BusinessException("Could not delete file: " + e.getMessage(), "FILE_DELETE_ERROR");
        }
    }
}