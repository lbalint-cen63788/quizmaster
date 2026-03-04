package cz.scrumdojo.quizmaster.aiassistant;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.Flow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AiAssistantServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
        public void generateQuestionReturnsMappedResponseAndUsesConfiguredModel() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        String assistantJson = """
            {
              "question": "Jaké je hlavní město České republiky?",
              "options": {"A": "Brno", "B": "Praha"},
              "correct": "B"
            }
            """;

        HttpResponse<String> completionResponse = response(200,
            "{" +
                "\"choices\":[{" +
                "\"message\":{" +
                "\"content\":" + objectMapper.writeValueAsString(assistantJson) +
                "}}]}"
        );

        AtomicReference<HttpRequest> completionRequestRef = new AtomicReference<>();

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenAnswer(invocation -> {
                HttpRequest request = invocation.getArgument(0);
                if (request.uri().getPath().endsWith("/chat/completions")) {
                    completionRequestRef.set(request);
                    return completionResponse;
                }
                throw new IllegalStateException("Unexpected URI: " + request.uri());
            });

        AiAssistantService service = new AiAssistantService(objectMapper, "token", httpClient);

        AiAssistantResponse result = service.generateQuestion("hlavni mesto");

        assertEquals("Jaké je hlavní město České republiky?", result.question());
        assertEquals("Praha", result.correctAnswerText());
        assertEquals("Brno", result.incorrectAnswerText());
        assertEquals("answer2", result.correctAnswer());

        HttpRequest completionRequest = completionRequestRef.get();
        String requestBody = requestBody(completionRequest);
        assertEquals(true, requestBody.contains("minimax/minimax-m2.5"));
        assertEquals(true, requestBody.contains("You are a quiz question generator."));
        assertEquals(true, requestBody.contains("hlavni mesto"));
        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    public void generateQuestionFailsOnEmptyPrompt() {
        AiAssistantService service = new AiAssistantService(objectMapper, "token", mock(HttpClient.class));

        AiAssistantException exception = assertThrows(AiAssistantException.class,
            () -> service.generateQuestion("   "));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    public void generateQuestionFailsWhenTokenMissing() {
        AiAssistantService service = new AiAssistantService(objectMapper, "", mock(HttpClient.class));

        AiAssistantException exception = assertThrows(AiAssistantException.class,
            () -> service.generateQuestion("topic"));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    public void generateQuestionFailsWhenCorrectOptionIsInvalid() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        String assistantJson = "{" +
            "\"question\":\"Q\"," +
            "\"options\":{\"A\":\"A\",\"B\":\"B\"}," +
            "\"correct\":\"C\"}";

        HttpResponse<String> completionResponse = response(200,
            "{" +
                "\"choices\":[{" +
                "\"message\":{" +
                "\"content\":" + objectMapper.writeValueAsString(assistantJson) +
                "}}]}"
        );

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(completionResponse);

        AiAssistantService service = new AiAssistantService(objectMapper, "token", httpClient);

        AiAssistantException exception = assertThrows(AiAssistantException.class,
            () -> service.generateQuestion("topic"));

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatus());
        assertEquals("AI assistant returned invalid correct option.", exception.getMessage());
    }

    @Test
    public void generateQuestionMapsTimeoutToGatewayTimeout() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenThrow(new HttpTimeoutException("timeout"));

        AiAssistantService service = new AiAssistantService(objectMapper, "token", httpClient);

        AiAssistantException exception = assertThrows(AiAssistantException.class,
            () -> service.generateQuestion("topic"));

        assertEquals(HttpStatus.GATEWAY_TIMEOUT, exception.getStatus());
    }

    @Test
        public void generateQuestionRetriesSameModelWhenRateLimited() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);

        HttpResponse<String> rateLimitResponse = response(429, """
            {"error": {"message": "rate"}}
            """);

        String assistantJson = """
            {
              "question": "Otázka?",
              "options": {"A": "Ano", "B": "Ne"},
              "correct": "A"
            }
            """;

        HttpResponse<String> completionResponse = response(200,
            "{" +
                "\"choices\":[{" +
                "\"message\":{" +
                "\"content\":" + objectMapper.writeValueAsString(assistantJson) +
                "}}]}"
        );

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(rateLimitResponse, completionResponse);

        AiAssistantService service = new AiAssistantService(objectMapper, "token", httpClient);

        AiAssistantResponse result = service.generateQuestion("topic");

        assertEquals("Otázka?", result.question());
        assertEquals("Ano", result.correctAnswerText());
        assertEquals("Ne", result.incorrectAnswerText());
        assertEquals("answer1", result.correctAnswer());
        verify(httpClient, times(2)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    private HttpResponse<String> response(int statusCode, String body) {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(statusCode);
        when(response.body()).thenReturn(body);
        return response;
    }

    private String requestBody(HttpRequest request) throws Exception {
        Optional<HttpRequest.BodyPublisher> maybePublisher = request.bodyPublisher();
        if (maybePublisher.isEmpty()) {
            return "";
        }

        HttpRequest.BodyPublisher publisher = maybePublisher.get();
        StringBuilder builder = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);
        CompletableFuture<Throwable> error = new CompletableFuture<>();

        publisher.subscribe(new Flow.Subscriber<>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(ByteBuffer item) {
                builder.append(StandardCharsets.UTF_8.decode(item.duplicate()));
            }

            @Override
            public void onError(Throwable throwable) {
                error.complete(throwable);
                latch.countDown();
            }

            @Override
            public void onComplete() {
                latch.countDown();
            }
        });

        latch.await(2, TimeUnit.SECONDS);
        if (error.isDone()) {
            throw new RuntimeException(error.get());
        }

        return builder.toString();
    }
}
