package ai.sprints.chatbot.model.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LlmApiMessageRequest {

    private String prompt;
    private Integer maxTokens;
}
