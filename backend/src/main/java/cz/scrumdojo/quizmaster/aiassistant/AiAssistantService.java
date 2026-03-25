package cz.scrumdojo.quizmaster.aiassistant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class AiAssistantService {

    private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODEL = "minimax/minimax-m2.5";
    private static final Duration TIMEOUT = Duration.ofSeconds(60);

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String apiToken;

    public AiAssistantService(
        ObjectMapper objectMapper,
        @Value("${ai.token:}") String apiToken
    ) {
        this.objectMapper = objectMapper;
        this.apiToken = apiToken.strip();
        this.httpClient = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    }

    public AiAssistantResponse generateQuestion(AiAssistantQuestionType type, String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Question must not be empty.");
        }
        if (apiToken == null || apiToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "AI token is not configured.");
        }

        try {
            String body = objectMapper.writeValueAsString(new ChatRequest(
                MODEL,
                new Message[]{new Message("system", type.getPrompt()), new Message("user", prompt)}
            ));

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENROUTER_URL))
                .timeout(TIMEOUT)
                .header("Authorization", "Bearer " + apiToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI assistant request failed.");
            }

            JsonNode root = objectMapper.readTree(response.body());
            String content = root.path("choices").path(0).path("message").path("content").asText("").trim();
            AssistantResponse assistantResponse = objectMapper.readValue(cleanAiResponse(content), AssistantResponse.class);

            return new AiAssistantResponse(type, assistantResponse.question(), assistantResponse.answers(), assistantResponse.correctAnswers());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI assistant request failed.");
        }
    }

    public String cleanAiResponse(String content) {
        if (StringUtils.isNotBlank(content)) {
            int openJsonBrace = StringUtils.indexOf(content, '{');
            int closeJsonBrace = StringUtils.lastIndexOf(content, '}');
            if (openJsonBrace >= 0 && closeJsonBrace >= 0) {
                return content.substring(openJsonBrace, closeJsonBrace + 1);
            }
        }

        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI assistant request failed.");
    }

    private record ChatRequest(String model, Message[] messages) {}

    private record Message(String role, String content) {}

    private record AssistantResponse(String question, String[] answers, int[] correctAnswers) {}
}
