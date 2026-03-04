package cz.scrumdojo.quizmaster.aiassistant;

import org.springframework.http.HttpStatus;

public class AiAssistantException extends RuntimeException {
    private final HttpStatus status;

    public AiAssistantException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
