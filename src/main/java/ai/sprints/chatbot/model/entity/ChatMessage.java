package ai.sprints.chatbot.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ChatMessage {

    @Id
    private String messageId;

    @ManyToOne
    private ChatSession session;

    private String content;

    private boolean isFromUser;

    private LocalDateTime timestamp;
}