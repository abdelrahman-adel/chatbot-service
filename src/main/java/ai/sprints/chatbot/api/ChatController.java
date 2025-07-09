package ai.sprints.chatbot.api;

import ai.sprints.chatbot.model.dto.ChatMessageRequest;
import ai.sprints.chatbot.model.dto.ChatMessageResponse;
import ai.sprints.chatbot.model.entity.ChatSession;
import ai.sprints.chatbot.service.ChatService;
import ai.sprints.chatbot.service.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final JwtUtil jwtUtil;

    @PostMapping("/session")
    public ResponseEntity<ChatSession> createSession(@RequestHeader("Authorization") String jwtToken) {
        String userId = jwtUtil.getUserIdFromJwt(jwtToken.replace("Bearer ", ""));
        log.info("Creating session for user: {}", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(chatService.createSession(userId));
    }

    @PostMapping("/message")
    public ResponseEntity<ChatMessageResponse> sendMessage(@Valid @RequestBody ChatMessageRequest request) throws Exception {
        log.info("Processing message for session: {}", request.getSessionId());
        return ResponseEntity.ok(chatService.sendMessage(request));
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<ChatMessageResponse>> getHistory(@PathVariable String sessionId) {
        log.info("Fetching history for session: {}", sessionId);
        return ResponseEntity.ok(chatService.getHistory(sessionId));
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.debug("Health check requested");
        return ResponseEntity.ok("Service is up and running");
    }
}