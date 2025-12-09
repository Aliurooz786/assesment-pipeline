package com.example.urooz.service;

import com.example.urooz.model.JudgmentMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service responsible for interacting with the LLM to extract structured legal
 * data from raw text.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LlmExtractionService {

    private final ChatLanguageModel chatLanguageModel;
    private final ObjectMapper objectMapper;

    private static final String EXTRACTION_PROMPT = """
            Act as a legal domain expert. Analyze the following legal judgment text.

            Extract the following fields and return the output STRICTLY in JSON format:
            1. title (Case Title)
            2. court (Court Name)
            3. date (Judgment Date)
            4. facts (Brief summary of facts)
            5. issues (List of legal issues raised)
            6. arguments (Map with keys 'petitioner' and 'respondent')
            7. ratio (The ratio decidendi)
            8. holding (The final verdict/holding)
            9. citations (List of cases cited)

            Return raw JSON only. Do not use markdown blocks.

            TEXT TO ANALYZE:
            {{text}}
            """;

    /**
     * Sends raw text to the LLM and parses the JSON response into JudgmentMetadata.
     *
     * @param rawText The text extracted from the PDF.
     * @return Structured JudgmentMetadata object.
     */
    public JudgmentMetadata extractMetadata(String rawText) {
        log.info("Initiating metadata extraction using LLM. Text length: {}", rawText.length());

        // Cap text length to avoid token limits.
        String truncatedText = rawText.length() > 20000 ? rawText.substring(0, 20000) : rawText;

        PromptTemplate template = PromptTemplate.from(EXTRACTION_PROMPT);
        Prompt prompt = template.apply(Map.of("text", truncatedText));

        try {
            String jsonResponse = chatLanguageModel.generate(prompt.text());
            log.debug("LLM Response received: {}", jsonResponse);

            String cleanJson = jsonResponse.replace("```json", "").replace("```", "").trim();
            return objectMapper.readValue(cleanJson, JudgmentMetadata.class);

        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON response from LLM", e);
            throw new RuntimeException("Invalid JSON structure received from AI service", e);
        } catch (Exception e) {
            log.error("Error occurred during LLM extraction", e);
            throw new RuntimeException("LLM Extraction failed", e);
        }
    }
}