package ai.sprints.chatbot.repository;

import ai.sprints.chatbot.model.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, String> {
}