package cz.scrumdojo.quizmaster.aiassistant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

@Service
public class AiAssistantService {

    private static final String OPENROUTER_BASE_URL = "https://openrouter.ai/api/v1";
    private static final String OPENROUTER_MODEL = "minimax/minimax-m2.5";
    private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(20);
    private static final int COMPLETION_RETRY_COUNT = 2;
    private static final long RETRY_DELAY_MS = 400;
        private static final String SYSTEM_MESSAGE = """
                You are a quiz question generator.

                The user will provide a TOPIC. Your task is to generate exactly:
                - 1 question related to the given topic
                - 2 answer options (A and B)
                - exactly 1 correct answer and 1 incorrect but plausible answer
                - indicate which option is correct using the field "correct"

                Return the output strictly as valid JSON with no additional text:

                {
                    "question": "...?",
                    "options": {
                        "A": "...",
                        "B": "..."
                    },
                    "correct": "A"
                }

                Rules:
                - The question must be clear, factual, and verifiable.
                - Prefer a single concise sentence for the question.
                - Both answer options should be similar in length and style.
                - The incorrect option should sound believable but must be clearly wrong.
                - Do not include explanations, comments, or formatting outside the JSON.
                """;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String apiToken;

    @Autowired
    public AiAssistantService(
        ObjectMapper objectMapper,
        @Value("${ai.token:}") String apiToken
    ) {
        this(
            objectMapper,
            apiToken,
            HttpClient.newBuilder()
                .connectTimeout(HTTP_TIMEOUT)
                .build()
        );
    }

    AiAssistantService(
        ObjectMapper objectMapper,
        String apiToken,
        HttpClient httpClient
    ) {
        this.objectMapper = objectMapper;
        this.apiToken = apiToken;
        this.httpClient = httpClient;
    }

    public AiAssistantResponse generateQuestion(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new AiAssistantException(HttpStatus.BAD_REQUEST, "Question must not be empty.");
        }

        if (apiToken == null || apiToken.isBlank()) {
            throw new AiAssistantException(HttpStatus.SERVICE_UNAVAILABLE, "AI token is not configured.");
        }

        try {
            return generateCompletionWithRetry(prompt, OPENROUTER_MODEL, COMPLETION_RETRY_COUNT);
        } catch (HttpTimeoutException e) {
            throw new AiAssistantException(HttpStatus.GATEWAY_TIMEOUT, "AI assistant timed out.");
        } catch (IOException e) {
            throw new AiAssistantException(HttpStatus.BAD_GATEWAY, "AI assistant is unreachable.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AiAssistantException(HttpStatus.SERVICE_UNAVAILABLE, "AI assistant request was interrupted.");
        }
    }

    private AiAssistantResponse generateCompletionWithRetry(String topic, String model, int maxAttempts)
        throws IOException, InterruptedException {

        AiAssistantException lastRateLimit = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return generateCompletion(topic, model);
            } catch (AiAssistantException e) {
                if (e.getStatus() != HttpStatus.TOO_MANY_REQUESTS) {
                    throw e;
                }

                lastRateLimit = e;
                if (attempt < maxAttempts) {
                    Thread.sleep(RETRY_DELAY_MS);
                }
            }
        }

        throw lastRateLimit == null
            ? new AiAssistantException(HttpStatus.TOO_MANY_REQUESTS, "AI assistant rate limit reached.")
            : lastRateLimit;
    }

    private AiAssistantResponse generateCompletion(String topic, String model) throws IOException, InterruptedException {
        String body = objectMapper.writeValueAsString(new OpenRouterChatRequest(
            model,
            new Message[]{
                new Message("system", SYSTEM_MESSAGE),
                new Message("user", topic)
            }
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

        return parseQuizJson(content);
    }

    private AiAssistantResponse parseQuizJson(String contentJson) {
        try {
            JsonNode node = objectMapper.readTree(extractJson(contentJson));

            String question = node.path("question").asText("").trim();
            String optionA = node.path("options").path("A").asText("").trim();
            String optionB = node.path("options").path("B").asText("").trim();
            String correct = node.path("correct").asText("").trim();

            if (question.isEmpty()) {
                throw new AiAssistantException(HttpStatus.BAD_GATEWAY, "AI assistant returned empty question.");
            }
            if (optionA.isEmpty() || optionB.isEmpty()) {
                throw new AiAssistantException(HttpStatus.BAD_GATEWAY, "AI assistant returned invalid options.");
            }
            if (!"A".equals(correct) && !"B".equals(correct)) {
                throw new AiAssistantException(HttpStatus.BAD_GATEWAY, "AI assistant returned invalid correct option.");
            }
            if (optionA.equalsIgnoreCase(optionB)) {
                throw new AiAssistantException(HttpStatus.BAD_GATEWAY, "AI assistant returned duplicate answers.");
            }

            String correctText = "A".equals(correct) ? optionA : optionB;
            String incorrectText = "A".equals(correct) ? optionB : optionA;
            String correctAnswer = "A".equals(correct) ? "answer1" : "answer2";
            return new AiAssistantResponse(question, correctText, incorrectText, correctAnswer);
        } catch (AiAssistantException e) {
            throw e;
        } catch (Exception e) {
            throw new AiAssistantException(HttpStatus.BAD_GATEWAY, "AI assistant returned invalid JSON.");
        }
    }

    private String extractJson(String rawContent) {
        String content = rawContent == null ? "" : rawContent.trim();
        if (content.isEmpty()) {
            return content;
        }

        if (content.startsWith("```")) {
            int firstLineEnd = content.indexOf('\n');
            int lastFence = content.lastIndexOf("```");
            if (firstLineEnd >= 0 && lastFence > firstLineEnd) {
                content = content.substring(firstLineEnd + 1, lastFence).trim();
            }
        }

        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return content.substring(start, end + 1).trim();
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

    private record OpenRouterChatRequest(String model, Message[] messages) {}

    private record Message(String role, String content) {}
}
