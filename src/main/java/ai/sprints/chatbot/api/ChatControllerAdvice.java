package ai.sprints.chatbot.api;

import ai.sprints.chatbot.exception.InvalidJwtToken;
import ai.sprints.chatbot.exception.LlmApiUnavailableException;
import ai.sprints.chatbot.exception.NoMessagesFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ChatControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Invalid request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(LlmApiUnavailableException.class)
    public ResponseEntity<String> handleLlmApiUnavailable(LlmApiUnavailableException ex) {
        log.error("LLM API unavailable: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("LLM API is currently unavailable");
    }

    @ExceptionHandler(NoMessagesFoundException.class)
    public ResponseEntity<String> handleNoMessagesFound(NoMessagesFoundException ex) {
        log.error("No messages found for the given session ID: {}", ex.getSessionId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No messages found for the given session ID");
    }

    @ExceptionHandler(InvalidJwtToken.class)
    public ResponseEntity<String> handleInvalidJwtToken(InvalidJwtToken ex) {
        log.error("Invalid JWT token: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
    }
}
