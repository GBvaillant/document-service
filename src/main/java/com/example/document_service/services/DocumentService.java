package com.example.document_service.services;

import com.example.document_service.model.DocumentModelData;
import com.example.document_service.repositories.DocumentDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final S3Client s3Client;
    private final DocumentDataRepository documentDataRepository;

    @Value("${aws.credentials.endpoint}")
    private String baseUrl;

    @Autowired
    public DocumentService(S3Client s3Client, DocumentDataRepository documentDataRepository) {
        this.s3Client = s3Client;
        this.documentDataRepository = documentDataRepository;
    }

    public List<String> listDocuments(String bucketName) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(request);

        return response.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

    public void uploadDocumentS3 (String bucketName, String key, Path filePath) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(filePath));

        String urlS3 = baseUrl + bucketName + "/" + key;

        DocumentModelData document = new DocumentModelData();
        document.setName(key);
        document.setLink(urlS3);
        document.setCreateDate(LocalDateTime.now());

        documentDataRepository.save(document);

//        DocumentModelData document = DocumentModelData.builder()
//                .name(key)
//                .link(urlS3)
//                .createDate(LocalDateTime.now())
//                .build();
//
//        documentDataRepository.save(document);

    }

}
