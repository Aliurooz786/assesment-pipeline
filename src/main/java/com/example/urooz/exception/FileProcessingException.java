package com.example.urooz.exception;

/**
 * Custom exception thrown when file processing (parsing/reading) fails.
 * This ensures strict error categorisation in the application.
 */
public class FileProcessingException extends RuntimeException {
    public FileProcessingException(String message) {
        super(message);
    }

    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}