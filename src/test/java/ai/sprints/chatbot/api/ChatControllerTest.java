package ai.sprints.chatbot.api;

import ai.sprints.chatbot.model.dto.ChatMessageRequest;
import ai.sprints.chatbot.model.dto.ChatMessageResponse;
import ai.sprints.chatbot.service.ChatService;
import ai.sprints.chatbot.service.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/chat/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Service is up and running"));
    }

    @Test
    void testSendMessage() throws Exception {
        ChatMessageRequest request = new ChatMessageRequest();
        request.setSessionId("session1");
        request.setContent("Hello");
        ChatMessageResponse response = new ChatMessageResponse();
        response.setMessageId("msg1");
        response.setSessionId("session1");
        response.setContent("Hi!");
        response.setFromUser(false);

        when(chatService.sendMessage(any(ChatMessageRequest.class))).thenReturn(response);

        mockMvc.perform(post("/chat/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":\"session1\",\"content\":\"Hello\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"messageId\":\"msg1\",\"sessionId\":\"session1\",\"content\":\"Hi!\",\"isFromUser\":false}"));
    }
}