package com.example.urooz.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for processing documents and storing their vector
 * embeddings.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VectorStoreService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    /**
     * Processes text content, splits it into segments, and stores embeddings in the
     * vector database.
     *
     * @param text  The raw text content of the document.
     * @param docId The unique identifier of the document.
     */
    public void processAndStore(String text, String docId) {
        log.info("Starting vector embedding processing for Document ID: {}", docId);

        if (text == null || text.isEmpty()) {
            log.warn("Vector storage skipped: Document text is null or empty.");
            return;
        }

        Document document = Document.from(text, Metadata.from("document_id", docId));

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(500, 50))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        ingestor.ingest(document);

        log.info("Vector embeddings successfully stored in Qdrant for Doc ID: {}", docId);
    }
}