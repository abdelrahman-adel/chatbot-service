package ai.sprints.chatbot.exception;

public class LlmApiUnavailableException extends RuntimeException {

    public LlmApiUnavailableException(Exception e) {
        super(e);
    }
}
