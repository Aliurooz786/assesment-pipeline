package com.example.urooz.controller;

import com.example.urooz.model.JudgmentMetadata;
import com.example.urooz.repository.JudgmentRepository;
import com.example.urooz.service.AnswerGeneratorService;
import com.example.urooz.service.LlmExtractionService;
import com.example.urooz.service.PdfExtractionService;
import com.example.urooz.service.SearchService;
import com.example.urooz.service.VectorStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for handling judgment extraction and search operations.
 */
@RestController
@RequestMapping("/api/v1/judgment")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class JudgmentController {

    private final PdfExtractionService pdfExtractionService;
    private final LlmExtractionService llmExtractionService;
    private final JudgmentRepository judgmentRepository;
    private final VectorStoreService vectorStoreService;
    private final SearchService searchService;
    private final AnswerGeneratorService answerGeneratorService;

    /**
     * Extracts metadata from an uploaded PDF judgment file and saves it.
     *
     * @param file The PDF file containing the judgment.
     * @return The saved judgment metadata.
     */
    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<JudgmentMetadata> extractAndSave(@RequestParam("file") MultipartFile file) {
        log.info("Processing judgment file: {}", file.getOriginalFilename());

        String rawText = pdfExtractionService.extractText(file);
        JudgmentMetadata metadata = llmExtractionService.extractMetadata(rawText);
        metadata.setOriginalText(rawText);

        JudgmentMetadata savedData = judgmentRepository.save(metadata);
        log.info("Judgment metadata persisted to database with ID: {}", savedData.getId());

        try {
            vectorStoreService.processAndStore(rawText, savedData.getId());
        } catch (Exception e) {
            log.error("Failed to process and store vector embeddings for document ID: {}", savedData.getId(), e);
        }
        return ResponseEntity.ok(savedData);
    }

    /**
     * Searches for relevant legal information based on a user query.
     *
     * @param query The search query string.
     * @return A map containing the AI-generated answer.
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, String>> searchLegalQuery(@RequestParam("query") String query) {
        log.info("Processing search query: {}", query);

        List<String> relevantChunks = searchService.search(query);
        log.debug("Retrieved {} relevant text segments for query.", relevantChunks.size());

        String aiAnswer = answerGeneratorService.generateAnswer(query, relevantChunks);

        return ResponseEntity.ok(Map.of("answer", aiAnswer));
    }
}