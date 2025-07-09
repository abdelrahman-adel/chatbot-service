package ai.sprints.chatbot.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageResponse {

    private String messageId;
    private String sessionId;
    private String content;
    private boolean isFromUser;
    private LocalDateTime timestamp;
}