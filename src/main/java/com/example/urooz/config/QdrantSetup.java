package com.example.urooz.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class QdrantSetup {

    // Qdrant REST URL (Port 6333 use karega)
    private final String QDRANT_URL = "http://localhost:6333/collections/legal_judgments";

    @PostConstruct
    public void init() {
        createCollectionIfNotExists();
    }

    private void createCollectionIfNotExists() {
        RestTemplate restTemplate = new RestTemplate();

        try {
            // 1. Check karo ki collection pehle se hai ya nahi
            restTemplate.getForEntity(QDRANT_URL, String.class);
            log.info("✅ Qdrant Collection 'legal_judgments' already exists.");

        } catch (HttpClientErrorException.NotFound e) {
            // 2. Agar 404 aaya (Nahi mili), toh create karo
            log.info("⚠️ Collection not found. Creating 'legal_judgments'...");

            Map<String, Object> body = new HashMap<>();

            // "vectors" configuration
            Map<String, Object> vectors = new HashMap<>();
            vectors.put("size", 384); // AllMiniLM model ka vector size 384 hota hai
            vectors.put("distance", "Cosine"); // Search ke liye Maths formula

            body.put("vectors", vectors);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            try {
                // PUT request bhej kar collection banao
                restTemplate.put(QDRANT_URL, request);
                log.info("✅ Successfully created Qdrant collection: legal_judgments");
            } catch (Exception ex) {
                log.error("❌ Failed to create Qdrant collection: {}", ex.getMessage());
            }
        } catch (Exception e) {
            log.error("❌ Error checking Qdrant status: Is Docker running? {}", e.getMessage());
        }
    }
}