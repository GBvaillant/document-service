package com.example.document_service.controllers;

import com.example.document_service.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class DocumentController {

    private final DocumentService documentService;
    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload/{bucketName}")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file,
                                                 @PathVariable String bucketName) {
        String key = "files/" + file.getOriginalFilename();

        try {
            Path tempFile = Files.createTempFile("temp", file.getOriginalFilename());
            file.transferTo(tempFile);

            documentService.uploadDocumentS3(bucketName, key, tempFile);
            log.info("UPLOAD FILE {} TO BUCKET {}", key, bucketName);

            Files.delete(tempFile);

            return ResponseEntity.ok("File uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload file");
        }
    }
}
