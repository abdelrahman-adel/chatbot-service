package ai.sprints.chatbot.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class ChatSession {

    @Id
    private String sessionId;

    private String userId;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "session", fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    private List<ChatMessage> messages;
}