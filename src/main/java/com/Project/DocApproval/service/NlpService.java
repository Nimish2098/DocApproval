package com.Project.DocApproval.service;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NlpService {

    public Set<String> extractTechnicalNouns(String text) {
        try (InputStream modelIn = new ClassPathResource("en-pos-maxent.bin").getInputStream()) {
            POSModel model = new POSModel(modelIn);
            POSTaggerME tagger = new POSTaggerME(model);

            // 1. Tokenize the input text
            String[] rawTokens = WhitespaceTokenizer.INSTANCE.tokenize(text.toLowerCase());

            // 2. Cleanup: Remove punctuation attached to words
            List<String> cleanedTokensList = new ArrayList<>();
            for (String token : rawTokens) {
                // Regex: Removes anything that isn't a letter or number
                String clean = token.replaceAll("[^a-zA-Z0-9]", "");
                if (!clean.isEmpty()) {
                    cleanedTokensList.add(clean);
                }
            }

            String[] tokens = cleanedTokensList.toArray(new String[0]);

            // 3. Generate POS tags (NN = Noun, NNP = Proper Noun)
            String[] tags = tagger.tag(tokens);

            Set<String> filteredKeywords = new HashSet<>();
            for (int i = 0; i < tags.length; i++) {
                // Only keep Nouns and Proper Nouns
                if (tags[i].startsWith("NN")) {
                    filteredKeywords.add(tokens[i]);
                }
            }

            return filteredKeywords;
        } catch (IOException e) {
            throw new RuntimeException("NLP Model failed to load. Ensure en-pos-maxent.bin is in src/main/resources", e);
        }
    }
}