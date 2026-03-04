package cz.scrumdojo.quizmaster.question;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Service
public class TestNumericalQuestionService {

    private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(5);
    private static final int MAX_ATTEMPTS = 2;
    private static final long RETRY_DELAY_MS = 200;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String apiKey;

    public TestNumericalQuestionService(
        WebClient.Builder webClientBuilder,
        ObjectMapper objectMapper,
        @Value("${external.api.base-url:}") String baseUrl,
        @Value("${external.api.key:}") String apiKey
    ) {
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.webClient = webClientBuilder.build();
    }

    public TestNumericalQuestionResponse getTestNumericalQuestion() {
        validateConfiguration();

        ExternalApiException lastTransientError = null;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                return fetchFromExternalApi();
            } catch (ExternalApiException e) {
                if (!isRetryable(e) || attempt == MAX_ATTEMPTS) {
                    throw e;
                }
                lastTransientError = e;
                sleepBackoff();
            }
        }

        throw lastTransientError == null
            ? new ExternalApiException(HttpStatus.BAD_GATEWAY, "External API request failed.")
            : lastTransientError;
    }

    private TestNumericalQuestionResponse fetchFromExternalApi() {
        WebClient.RequestHeadersSpec<?> request = webClient.get()
            .uri(baseUrl + "/test-numerical-question")
            .accept(MediaType.APPLICATION_JSON);

        if (!apiKey.isBlank()) {
            request = request.header("Authorization", "Bearer " + apiKey);
        }

        try {
            ExternalPayload payload = request.exchangeToMono(response -> {
                HttpStatus status = HttpStatus.resolve(response.statusCode().value());
                if (status == null) {
                    return response.createException().flatMap(ex ->
                        reactor.core.publisher.Mono.error(new ExternalApiException(
                            HttpStatus.BAD_GATEWAY,
                            "External API returned unknown status code."
                        ))
                    );
                }

                if (status.is2xxSuccessful()) {
                    return response.bodyToMono(String.class)
                        .map(this::parsePayload);
                }

                return response.bodyToMono(String.class)
                    .defaultIfEmpty("")
                    .flatMap(body -> reactor.core.publisher.Mono.error(toException(status)));
            }).timeout(HTTP_TIMEOUT).block();

            if (payload == null) {
                throw new ExternalApiException(HttpStatus.BAD_GATEWAY, "External API returned empty response.");
            }

            String question = payload.question() == null ? "" : payload.question().trim();
            String correctAnswer = payload.correctAnswer() == null ? "" : payload.correctAnswer().trim();

            if (question.isEmpty() || correctAnswer.isEmpty()) {
                throw new ExternalApiException(HttpStatus.BAD_GATEWAY, "External API returned invalid JSON.");
            }

            return new TestNumericalQuestionResponse(question, correctAnswer);
        } catch (ExternalApiException e) {
            throw e;
        } catch (WebClientRequestException e) {
            if (isTimeoutThrowable(e)) {
                throw new ExternalApiException(HttpStatus.GATEWAY_TIMEOUT, "External API timed out.");
            }
            throw new ExternalApiException(HttpStatus.BAD_GATEWAY, "External API is unreachable.");
        } catch (Exception e) {
            if (isTimeoutThrowable(e)) {
                throw new ExternalApiException(HttpStatus.GATEWAY_TIMEOUT, "External API timed out.");
            }
            throw new ExternalApiException(HttpStatus.BAD_GATEWAY, "External API returned invalid JSON.");
        }
    }

    private ExternalPayload parsePayload(String body) {
        try {
            return objectMapper.readValue(body, ExternalPayload.class);
        } catch (Exception e) {
            throw new ExternalApiException(HttpStatus.BAD_GATEWAY, "External API returned invalid JSON.");
        }
    }

    private ExternalApiException toException(HttpStatus status) {
        if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN) {
            return new ExternalApiException(status, "External API authentication failed.");
        }
        if (status == HttpStatus.NOT_FOUND) {
            return new ExternalApiException(HttpStatus.NOT_FOUND, "Requested resource was not found in external API.");
        }
        if (status == HttpStatus.BAD_REQUEST) {
            return new ExternalApiException(HttpStatus.BAD_REQUEST, "External API rejected the request.");
        }

        return new ExternalApiException(HttpStatus.BAD_GATEWAY, "External API request failed.");
    }

    private boolean isRetryable(ExternalApiException e) {
        return e.getStatus() == HttpStatus.BAD_GATEWAY || e.getStatus() == HttpStatus.GATEWAY_TIMEOUT;
    }

    private void validateConfiguration() {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new ExternalApiException(HttpStatus.BAD_GATEWAY, "External API base URL is not configured.");
        }
    }

    private void sleepBackoff() {
        try {
            Thread.sleep(RETRY_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExternalApiException(HttpStatus.SERVICE_UNAVAILABLE, "External API request was interrupted.");
        }
    }

    private boolean isTimeoutThrowable(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof TimeoutException || current instanceof SocketTimeoutException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private record ExternalPayload(String question, String correctAnswer) {}
}
