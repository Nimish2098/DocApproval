package com.Project.DocApproval.controller;

import com.Project.DocApproval.model.Document;
import com.Project.DocApproval.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/api/document")
    public void uploadDocument(@RequestBody Document document){
        documentService.createDocument(document);
    }

    @GetMapping("/api/document")
    public List<Document> getAllDocuments(){
        return documentService.getAllDocuments();
    }

}
