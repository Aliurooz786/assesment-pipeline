package com.example.urooz.service;

import com.example.urooz.exception.FileProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service responsible for parsing raw PDF files and extracting text content.
 * Utilizes Apache PDFBox for reliable text stripping.
 */
@Service
@Slf4j
public class PdfExtractionService {

    /**
     * Extracts raw text from a given PDF MultipartFile.
     *
     * @param file The uploaded PDF file from the controller.
     * @return A String containing the full text of the PDF.
     * @throws FileProcessingException if the file is empty, not a PDF, or
     *                                 unreadable.
     */
    public String extractText(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.error("Upload attempt failed: File is null or empty.");
            throw new FileProcessingException("Uploaded file cannot be empty.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            log.warn("Invalid file type uploaded: {}", contentType);
        }

        log.info("Starting PDF extraction for file: {}", file.getOriginalFilename());
        long startTime = System.currentTimeMillis();

        try (PDDocument document = PDDocument.load(file.getInputStream())) {

            if (document.isEncrypted()) {
                log.warn("The uploaded PDF is encrypted. Attempting to parse...");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            if (text != null) {
                text = text.trim();
            }

            long duration = System.currentTimeMillis() - startTime;
            log.info("Successfully extracted {} characters in {} ms.",
                    text != null ? text.length() : 0, duration);

            return text;

        } catch (IOException e) {
            log.error("Failed to parse PDF file: {}", file.getOriginalFilename(), e);
            throw new FileProcessingException("Error parsing PDF content", e);
        }
    }
}