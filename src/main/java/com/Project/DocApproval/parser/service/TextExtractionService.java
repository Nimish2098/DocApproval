package com.Project.DocApproval.parser.service;

import com.Project.DocApproval.analysis.exception.AnalysisException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
@Service
public class TextExtractionService {

    // Reuse a single Tika instance for better performance
    private final Tika tika = new Tika();

    public String extractText(MultipartFile file)  throws IOException, TikaException {

        try(InputStream ir = file.getInputStream()){
            return tika.parseToString(ir);
        }

    }
}