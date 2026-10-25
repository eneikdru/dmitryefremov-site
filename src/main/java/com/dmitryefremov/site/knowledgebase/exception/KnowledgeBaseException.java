package com.dmitryefremov.site.knowledgebase.exception;

public class KnowledgeBaseException extends RuntimeException {
    public KnowledgeBaseException(String message) {
        super(message);
    }

    public KnowledgeBaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
