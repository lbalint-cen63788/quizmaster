package cz.scrumdojo.quizmaster.aiassistant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class AiAssistantService {

    private static final String OPENROUTER_BASE_URL = "https://openrouter.ai/api/v1";
    private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(20);
    private static final BigDecimal HIGH_PRICE = new BigDecimal("999999999");

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String apiToken;

    public AiAssistantService(
        ObjectMapper objectMapper,
        @Value("${ai.token:}") String apiToken
    ) {
        this.objectMapper = objectMapper;
        this.apiToken = apiToken;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(HTTP_TIMEOUT)
            .build();
    }

    public String generateQuestion(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new AiAssistantException(HttpStatus.BAD_REQUEST, "Question must not be empty.");
        }

        if (apiToken == null || apiToken.isBlank()) {
            throw new AiAssistantException(HttpStatus.SERVICE_UNAVAILABLE, "AI token is not configured.");
        }

        try {
            String cheapestModel = fetchCheapestModel();
            return generateCompletion(prompt, cheapestModel);
        } catch (HttpTimeoutException e) {
            throw new AiAssistantException(HttpStatus.GATEWAY_TIMEOUT, "AI assistant timed out.");
        } catch (IOException e) {
            throw new AiAssistantException(HttpStatus.BAD_GATEWAY, "AI assistant is unreachable.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AiAssistantException(HttpStatus.SERVICE_UNAVAILABLE, "AI assistant request was interrupted.");
        }
    }

    private String fetchCheapestModel() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(OPENROUTER_BASE_URL + "/models"))
            .timeout(HTTP_TIMEOUT)
            .header("Authorization", "Bearer " + apiToken)
            .header("Accept", "application/json")
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ensureSuccessOrThrow(response, "Failed to fetch available models.");

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode models = root.path("data");

        if (!models.isArray() || models.isEmpty()) {
            throw new AiAssistantException(HttpStatus.BAD_GATEWAY, "No AI models available.");
        }

        Optional<String> cheapestModel = stream(models)
            .filter(model -> model.path("id").isTextual())
            .min(Comparator.comparing(this::modelPrice))
            .map(model -> model.path("id").asText());

        return cheapestModel.orElseThrow(() ->
            new AiAssistantException(HttpStatus.BAD_GATEWAY, "No AI models available."));
    }

    private String generateCompletion(String prompt, String model) throws IOException, InterruptedException {
        String body = objectMapper.writeValueAsString(new OpenRouterChatRequest(
            model,
            new Message[]{new Message("user", prompt)}
        ));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(OPENROUTER_BASE_URL + "/chat/completions"))
            .timeout(HTTP_TIMEOUT)
            .header("Authorization", "Bearer " + apiToken)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ensureSuccessOrThrow(response, "Failed to generate AI response.");

        JsonNode root = objectMapper.readTree(response.body());
        String content = root.path("choices").path(0).path("message").path("content").asText("").trim();

        if (content.isEmpty()) {
            throw new AiAssistantException(HttpStatus.BAD_GATEWAY, "AI assistant returned an empty response.");
        }

        return content;
    }

    private void ensureSuccessOrThrow(HttpResponse<String> response, String defaultMessage) {
        int statusCode = response.statusCode();

        if (statusCode >= 200 && statusCode < 300) {
            return;
        }

        if (statusCode == 429) {
            throw new AiAssistantException(HttpStatus.TOO_MANY_REQUESTS, "AI assistant rate limit reached.");
        }

        if (statusCode == 401 || statusCode == 403) {
            throw new AiAssistantException(HttpStatus.UNAUTHORIZED, "AI token is invalid.");
        }

        String message = extractErrorMessage(response.body());
        throw new AiAssistantException(HttpStatus.BAD_GATEWAY, message.isBlank() ? defaultMessage : message);
    }

    private String extractErrorMessage(String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            JsonNode error = root.path("error");

            if (error.path("message").isTextual()) {
                return error.path("message").asText();
            }

            if (root.path("message").isTextual()) {
                return root.path("message").asText();
            }

            return "";
        } catch (Exception ignored) {
            return "";
        }
    }

    private Stream<JsonNode> stream(JsonNode arrayNode) {
        return StreamSupport.stream(arrayNode.spliterator(), false);
    }

    private BigDecimal modelPrice(JsonNode model) {
        JsonNode pricing = model.path("pricing");
        BigDecimal promptPrice = parsePrice(pricing.path("prompt").asText());
        BigDecimal completionPrice = parsePrice(pricing.path("completion").asText());
        return promptPrice.add(completionPrice);
    }

    private BigDecimal parsePrice(String value) {
        if (value == null || value.isBlank()) {
            return HIGH_PRICE;
        }

        try {
            return new BigDecimal(value.trim());
        } catch (Exception ignored) {
            return HIGH_PRICE;
        }
    }

    private record OpenRouterChatRequest(String model, Message[] messages) {}

    private record Message(String role, String content) {}
}
