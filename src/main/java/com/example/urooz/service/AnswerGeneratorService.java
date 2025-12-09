package com.example.urooz.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for generating AI-based answers using retrieved legal
 * context.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AnswerGeneratorService {

    private final ChatLanguageModel chatLanguageModel;

    private static final String ANSWER_PROMPT = """
            You are a Legal AI Assistant specializing in Indian Case Law.

            Your task is to answer the user's question based ONLY on the provided legal context segments.

            Guidelines:
            1. Structure your answer with clear Headings and Bullet points.
            2. Do not hallucinate. If the answer is not in the context, state that the relevant information is not found in the documents.
            3. Maintain a professional legal tone while ensuring clarity.
            4. Cite the page number or paragraph if available in the text.

            ---
            LEGAL CONTEXT:
            {{context}}
            ---

            USER QUESTION: {{question}}

            YOUR STRUCTURED ANSWER:
            """;

    /**
     * Generates a structured answer using the LLM based on user query and relevant
     * document chunks.
     *
     * @param userQuery      The question asked by the user.
     * @param relevantChunks List of relevant text segments retrieved from the
     *                       vector store.
     * @return The generated answer string.
     */
    public String generateAnswer(String userQuery, List<String> relevantChunks) {
        log.info("Generating AI answer for query: {}", userQuery);

        if (relevantChunks.isEmpty()) {
            log.warn("No relevant chunks found for query: {}", userQuery);
            return "No relevant information found in the provided documents to answer this specific question.";
        }

        String joinedContext = String.join("\n\n", relevantChunks);

        PromptTemplate template = PromptTemplate.from(ANSWER_PROMPT);
        Map<String, Object> variables = new HashMap<>();
        variables.put("context", joinedContext);
        variables.put("question", userQuery);

        Prompt prompt = template.apply(variables);

        try {
            return chatLanguageModel.generate(prompt.text());
        } catch (Exception e) {
            log.error("Error occurred during AI answer generation", e);
            return "An error occurred while generating the answer. Please try again later.";
        }
    }
}