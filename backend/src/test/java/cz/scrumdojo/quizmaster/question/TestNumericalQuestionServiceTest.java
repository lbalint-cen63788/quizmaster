package cz.scrumdojo.quizmaster.question;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestNumericalQuestionServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void returnsMappedResponse() {
        ExchangeFunction exchangeFunction = request -> Mono.just(
            ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("""
                    {"question":"How many regions does Czechia have?","correctAnswer":"14"}
                    """)
                .build()
        );

        TestNumericalQuestionService service = new TestNumericalQuestionService(
            WebClient.builder().exchangeFunction(exchangeFunction),
            objectMapper,
            "http://localhost:9999",
            ""
        );

        TestNumericalQuestionResponse response = service.getTestNumericalQuestion();

        assertEquals("How many regions does Czechia have?", response.question());
        assertEquals("14", response.correctAnswer());
    }

    @Test
    public void mapsUnauthorizedTo401() {
        ExchangeFunction exchangeFunction = request -> Mono.just(
            ClientResponse.create(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("{\"message\":\"unauthorized\"}")
                .build()
        );

        TestNumericalQuestionService service = new TestNumericalQuestionService(
            WebClient.builder().exchangeFunction(exchangeFunction),
            objectMapper,
            "http://localhost:9999",
            "secret"
        );

        ExternalApiException exception = assertThrows(ExternalApiException.class, service::getTestNumericalQuestion);

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    public void mapsNotFoundTo404() {
        ExchangeFunction exchangeFunction = request -> Mono.just(
            ClientResponse.create(HttpStatus.NOT_FOUND)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("{\"message\":\"not found\"}")
                .build()
        );

        TestNumericalQuestionService service = new TestNumericalQuestionService(
            WebClient.builder().exchangeFunction(exchangeFunction),
            objectMapper,
            "http://localhost:9999",
            ""
        );

        ExternalApiException exception = assertThrows(ExternalApiException.class, service::getTestNumericalQuestion);

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    public void mapsInvalidJsonTo502() {
        ExchangeFunction exchangeFunction = request -> Mono.just(
            ClientResponse.create(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("{not-json")
                .build()
        );

        TestNumericalQuestionService service = new TestNumericalQuestionService(
            WebClient.builder().exchangeFunction(exchangeFunction),
            objectMapper,
            "http://localhost:9999",
            ""
        );

        ExternalApiException exception = assertThrows(ExternalApiException.class, service::getTestNumericalQuestion);

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatus());
    }

    @Test
    public void mapsTimeoutTo504() {
        ExchangeFunction exchangeFunction = mock(ExchangeFunction.class);
        when(exchangeFunction.exchange(any()))
            .thenReturn(Mono.error(new IOException(new SocketTimeoutException("timeout"))));

        TestNumericalQuestionService service = new TestNumericalQuestionService(
            WebClient.builder().exchangeFunction(exchangeFunction),
            objectMapper,
            "http://localhost:9999",
            ""
        );

        ExternalApiException exception = assertThrows(ExternalApiException.class, service::getTestNumericalQuestion);

        assertEquals(HttpStatus.GATEWAY_TIMEOUT, exception.getStatus());
    }
}
