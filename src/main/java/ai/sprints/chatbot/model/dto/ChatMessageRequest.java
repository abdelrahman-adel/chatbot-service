package ai.sprints.chatbot.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatMessageRequest {

    @NotBlank
    private String sessionId;

    @NotBlank
    private String content;
}