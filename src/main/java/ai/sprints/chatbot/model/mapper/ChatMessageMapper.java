package ai.sprints.chatbot.model.mapper;

import ai.sprints.chatbot.model.dto.ChatMessageResponse;
import ai.sprints.chatbot.model.entity.ChatMessage;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageMapper {

    public ChatMessageResponse toDto(ChatMessage message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setMessageId(message.getMessageId());
        response.setSessionId(message.getSession().getSessionId());
        response.setContent(message.getContent());
        response.setFromUser(message.isFromUser());
        response.setTimestamp(message.getTimestamp());
        return response;
    }
}
