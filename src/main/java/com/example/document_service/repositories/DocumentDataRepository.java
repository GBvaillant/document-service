package com.example.document_service.repositories;

import com.example.document_service.model.DocumentModelData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentDataRepository extends MongoRepository<DocumentModelData, Long> {
}
