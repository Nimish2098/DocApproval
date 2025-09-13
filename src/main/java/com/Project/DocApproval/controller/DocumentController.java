package com.Project.DocApproval.controller;

import com.Project.DocApproval.model.Document;
import com.Project.DocApproval.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping(value = "/upload" ,consumes = "multipart/form-data")
    public ResponseEntity<String> uploadDocument (@RequestParam("file") MultipartFile file) throws IOException {
        documentService.saveFileInDatabase(file);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping (value = "/{id}" ,produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getDocument(@PathVariable Long id){
        Document doc = documentService.getFile(id);
        return new ResponseEntity<>(doc.getData(),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable Long id){
        documentService.deleteFile(id);
        return new ResponseEntity<>("File Deleted Successfully",HttpStatus.NO_CONTENT);
    }

    //Test Methods
    @GetMapping("/documents")
    public List<Document> getAllDocuments(){
        return documentService.getAllDocuments();
    }
}
