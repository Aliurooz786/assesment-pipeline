package com.example.urooz.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for AI models and vector store components.
 */
@Configuration
public class AIConfig {

    @Value("${gemini.api.key}")
    private String apiKey;

    /**
     * Configures the Gemini Chat Model.
     */
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Google Gemini API Key is missing!");
        }
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-2.0-flash")
                .temperature(0.0)
                .build();
    }

    /**
     * Configures the Embedding Model (AllMiniLM).
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    /**
     * Configures the Qdrant Vector Store.
     */
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return QdrantEmbeddingStore.builder()
                .collectionName("legal_judgments")
                .host("localhost")
                .port(6334)
                .build();
    }
}