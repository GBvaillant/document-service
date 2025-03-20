package com.example.document_service.services;

import com.example.document_service.model.DocumentModelData;
import com.example.document_service.repositories.DocumentDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);
    private final S3Client s3Client;
    private final DocumentDataRepository documentDataRepository;

    @Value("${aws.credentials.endpoint}")
    private String baseUrl;

    @Autowired
    public DocumentService(S3Client s3Client, DocumentDataRepository documentDataRepository) {
        this.s3Client = s3Client;
        this.documentDataRepository = documentDataRepository;
    }

//    public List<String> listDocumentsFiles(String bucketName) {
//        ListObjectsV2Request request = ListObjectsV2Request.builder()
//                .bucket(bucketName)
//                .prefix("files/")
//                .build();
//
//        ListObjectsV2Response response = s3Client.listObjectsV2(request);
//
//        return response.contents().stream()
//                .map(S3Object::key)
//                .collect(Collectors.toList());
//    }

    public List<DocumentModelData> listDocumentData () {
        return documentDataRepository.findAll();
    }

    public void uploadDocumentS3 (String bucketName, String key, Path filePath, String name) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(filePath));

        String urlS3 = baseUrl + "/" + bucketName + "/" + key;

        DocumentModelData document = DocumentModelData.builder()
                .name(name)
                .link(urlS3)
                .createDate(LocalDateTime.now())
                .build();

        documentDataRepository.save(document);
        log.info("SAVE DOCUMENT {} TO DATABASE", key);
    }

}
