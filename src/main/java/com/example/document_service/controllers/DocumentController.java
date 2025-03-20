package com.example.document_service.controllers;

import com.example.document_service.model.DocumentModelData;
import com.example.document_service.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

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
        String name = file.getOriginalFilename();
        String key = "files/" + name;

        try {
            Path tempFile = Files.createTempFile("temp", file.getOriginalFilename());
            file.transferTo(tempFile);

            documentService.uploadDocumentS3(bucketName, key, tempFile, name);
            log.info("UPLOAD FILE {} TO BUCKET {}", name, bucketName);

            Files.delete(tempFile);

            return ResponseEntity.ok("File uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload file");
        }
    }

//    @GetMapping("/list/{bucketName}")
//    public ResponseEntity<String> listDocuments(@PathVariable String bucketName) {
//        log.info("LIST FILES IN BUCKET {}", bucketName);
//        try {
//            documentService.listDocumentsFiles(bucketName);
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Failed to list files");
//        }
//        return ResponseEntity.ok("Files listed successfully");
//    }

    @GetMapping("/listDocuments")
    public ResponseEntity<List<DocumentModelData>> listDocuments() {
        log.info("LIST DATA FILES");
        try {
            List<DocumentModelData> documents = documentService.listDocumentData();
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            log.error("Failed to list files", e);
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }
}