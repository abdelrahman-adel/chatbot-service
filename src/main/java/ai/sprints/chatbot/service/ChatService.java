package ai.sprints.chatbot.service;

import ai.sprints.chatbot.exception.NoMessagesFoundException;
import ai.sprints.chatbot.model.dto.ChatMessageRequest;
import ai.sprints.chatbot.model.dto.ChatMessageResponse;
import ai.sprints.chatbot.model.entity.ChatMessage;
import ai.sprints.chatbot.model.entity.ChatSession;
import ai.sprints.chatbot.model.mapper.ChatMessageMapper;
import ai.sprints.chatbot.repository.ChatMessageRepository;
import ai.sprints.chatbot.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private static final String CACHE_PREFIX = "chat:history:";
    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final ChatMessageMapper chatMessageMapper;
    private final LlmService llmService;
    private final RedisTemplate<String, Object> redisTemplate;

    public ChatSession createSession(String userId) {
        ChatSession session = new ChatSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUserId(userId);
        session.setCreatedAt(LocalDateTime.now());
        sessionRepository.save(session);
        log.info("Created session: {}", session.getSessionId());
        return session;
    }

    public ChatMessageResponse sendMessage(ChatMessageRequest request) throws Exception {
        ChatSession session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        // Save user message
        ChatMessage userMessage = new ChatMessage();
        userMessage.setMessageId(UUID.randomUUID().toString());
        userMessage.setSession(session);
        userMessage.setContent(request.getContent());
        userMessage.setFromUser(true);
        userMessage.setTimestamp(LocalDateTime.now());
        messageRepository.save(userMessage);

        String llmResponse = getLlmResponseFromCacheOrCall(request);

        // Save assistant response
        ChatMessage assistantMessage = new ChatMessage();
        assistantMessage.setMessageId(UUID.randomUUID().toString());
        assistantMessage.setSession(session);
        assistantMessage.setContent(llmResponse);
        assistantMessage.setFromUser(false);
        assistantMessage.setTimestamp(LocalDateTime.now());
        messageRepository.save(assistantMessage);

        // Update session cache
        List<ChatMessage> messages = messageRepository.findBySessionSessionId(request.getSessionId());
        redisTemplate.opsForValue().set(CACHE_PREFIX + request.getSessionId(), messages, 24, TimeUnit.HOURS);

        return chatMessageMapper.toDto(assistantMessage);
    }

    public List<ChatMessageResponse> getHistory(String sessionId) {
        List<ChatMessage> messages = (List<ChatMessage>) redisTemplate.opsForValue().get(CACHE_PREFIX + sessionId);
        if (messages != null) {
            log.debug("Cache hit for session history: {}", sessionId);
            return messages.stream().map(chatMessageMapper::toDto).toList();
        }

        messages = messageRepository.findBySessionSessionId(sessionId);
        if (messages.isEmpty()) {
            throw new NoMessagesFoundException(sessionId);
        }
        redisTemplate.opsForValue().set(CACHE_PREFIX + sessionId, messages, 24, TimeUnit.HOURS);
        log.debug("Cache miss, stored session history: {}", sessionId);
        return messages.stream().map(chatMessageMapper::toDto).toList();
    }

    private String getLlmResponseFromCacheOrCall(ChatMessageRequest request) throws Exception {
        String cacheKey = CACHE_PREFIX + request.getSessionId() + ":" + request.getContent();
        String cachedResponse = (String) redisTemplate.opsForValue().get(cacheKey);
        String llmResponse;
        if (cachedResponse != null) {
            llmResponse = cachedResponse;
            log.debug("Cache hit for message: {}", request.getContent());
        } else {
            llmResponse = llmService.getLlmResponse(request.getContent());
            redisTemplate.opsForValue().set(cacheKey, llmResponse, 1, TimeUnit.HOURS);
            log.debug("Cache miss, stored LLM response for: {}", request.getContent());
        }
        return llmResponse;
    }
}