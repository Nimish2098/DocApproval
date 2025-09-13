package com.Project.DocApproval.service;

import com.Project.DocApproval.exceptions.MyFileNotFoundException;
import com.Project.DocApproval.model.Document;
import com.Project.DocApproval.repository.DocumentRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOError;
import java.io.IOException;
import java.util.List;

@Service

public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    public void saveFileInDatabase(MultipartFile multipartFile) throws IOException {
        Document doc = new Document(multipartFile.getOriginalFilename(),
                multipartFile.getContentType(),
                multipartFile.getBytes());

        documentRepository.save(doc);
    }

    public List<Document> getAllDocuments(){
        return documentRepository.findAll();
    }
    public void deleteFile(Long id){
        try {
            documentRepository.deleteById(id);
        } catch (Exception e) {
            throw new MyFileNotFoundException("File Not Found",e);
        }
    }
    public Document getFile(Long id){
        return documentRepository.findById(id).orElseThrow(()->new RuntimeException("Not Found"));
    }
}
