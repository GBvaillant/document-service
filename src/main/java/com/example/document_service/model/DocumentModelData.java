package com.example.document_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(value = "Document_Data")
public class DocumentModelData {

    private String name;
    private String link;
    private LocalDateTime createDate;

}
