package ai.sprints.chatbot.exception;

import lombok.Getter;

@Getter
public class NoMessagesFoundException extends RuntimeException {

    private final String sessionId;

    public NoMessagesFoundException(String sessionId) {
        this.sessionId = sessionId;
    }
}
