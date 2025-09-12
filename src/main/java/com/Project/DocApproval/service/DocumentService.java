package com.Project.DocApproval.service;

import com.Project.DocApproval.model.Document;
import com.Project.DocApproval.repository.DocumentRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    public void createDocument(Document document){
        documentRepository.save(document);
    }

    public List<Document> getAllDocuments(){
        return documentRepository.findAll();
    }

}
