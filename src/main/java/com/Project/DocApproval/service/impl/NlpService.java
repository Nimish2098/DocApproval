package com.Project.DocApproval.service.impl;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
public class NlpService {

    private POSModel posModel; // Load once, reuse many times

    @PostConstruct
    public void init() {
        try (InputStream modelIn = new ClassPathResource("en-pos-maxent.bin").getInputStream()) {
            this.posModel = new POSModel(modelIn);
            log.info("NLP POS Model loaded successfully.");
        } catch (IOException e) {
            log.error("Failed to load NLP model. Skills extraction will be disabled.");
            throw new RuntimeException("NLP Model missing: en-pos-maxent.bin", e);
        }
    }

    public Set<String> extractTechnicalNouns(String text) {
        if (text == null || text.isBlank()) return Collections.emptySet();

        POSTaggerME tagger = new POSTaggerME(posModel);

        // 1. Tokenize and clean in one pass
        String[] rawTokens = WhitespaceTokenizer.INSTANCE.tokenize(text.toLowerCase());
        List<String> tokensList = new ArrayList<>();

        for (String token : rawTokens) {
            String clean = token.replaceAll("[^a-zA-Z0-9-]", ""); // Keep hyphens for "Spring-Boot"
            if (!clean.isEmpty()) {
                tokensList.add(clean);
            }
        }

        String[] tokens = tokensList.toArray(new String[0]);
        String[] tags = tagger.tag(tokens);

        Set<String> filteredKeywords = new HashSet<>();
        for (int i = 0; i < tags.length; i++) {
            // Filter for Nouns (NN), Proper Nouns (NNP), or Plural Nouns (NNS)
            if (tags[i].startsWith("NN")) {
                filteredKeywords.add(tokens[i]);
            }
        }

        return filteredKeywords;
    }
}