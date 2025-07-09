package ai.sprints.chatbot.service;

import ai.sprints.chatbot.exception.LlmApiUnavailableException;
import ai.sprints.chatbot.model.dto.LlmApiMessageRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

import java.net.URI;

@Service
public class LlmService {

    private final SecretsManagerClient secretsManagerClient;
    private final WebClient webClient;
    private final String secretName;
    private final String llmUrl;

    public LlmService(SecretsManagerClient secretsManagerClient,
                      WebClient webClient,
                      @Value("${aws.secretsmanager.llm-secret-name}") String secretName,
                      @Value("${ai.sprints.chatbot.llm-url}") String llmUrl) {
        this.secretsManagerClient = secretsManagerClient;
        this.webClient = webClient;
        this.secretName = secretName;
        this.llmUrl = llmUrl;
    }

    @CircuitBreaker(name = "llmService", fallbackMethod = "fallbackLlmResponse")
    public String getLlmResponse(String message) throws Exception {
        // Retrieve LLM API key from Secrets Manager
        String apiKey = getApiKey();

        return webClient.post()
                .uri(URI.create(llmUrl))
                .headers(headers -> headers.set("Authorization", "Bearer " + apiKey))
                .header("Content-Type", "application/json")
                .bodyValue(LlmApiMessageRequest.builder().prompt(message).maxTokens(400).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private String getApiKey() {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();
        return secretsManagerClient.getSecretValue(request).secretString();
    }

    private void fallbackLlmResponse(Exception e) {
        throw new LlmApiUnavailableException(e);
    }
}