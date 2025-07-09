package ai.sprints.chatbot.repository;

import ai.sprints.chatbot.model.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findBySessionSessionId(String sessionId);
}