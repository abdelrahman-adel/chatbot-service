package ai.sprints.chatbot.service;

import ai.sprints.chatbot.exception.InvalidJwtToken;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    private final SecretsManagerClient secretsManagerClient;
    private final String secretName;

    public JwtUtil(SecretsManagerClient secretsManagerClient,
                   @Value("${aws.secretsmanager.jwt-secret-name}") String secretName) {
        this.secretsManagerClient = secretsManagerClient;
        this.secretName = secretName;
    }

    public String getUserIdFromJwt(String jwtToken) {
        try {
            SecretKey sec = Keys.hmacShaKeyFor(getJwtSecret().getBytes());
            JwtParser parser = Jwts.parser().verifyWith(sec).build();
            return parser.parseUnsecuredClaims(jwtToken).getPayload().get("sub", String.class);
        } catch (Exception e) {
            throw new InvalidJwtToken();
        }
    }

    private String getJwtSecret() {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();
        return secretsManagerClient.getSecretValue(request).secretString();
    }
}