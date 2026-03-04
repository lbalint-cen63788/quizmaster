package cz.scrumdojo.quizmaster.question;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestNumericalQuestionController.class)
@Import({TestNumericalQuestionService.class, TestNumericalQuestionControllerIntegrationTest.WebClientTestConfig.class})
public class TestNumericalQuestionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static MockWebServer mockWebServer;

    @BeforeAll
    static void beforeAll() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void afterAll() throws Exception {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("external.api.base-url", () -> mockWebServer.url("/").toString().replaceAll("/$", ""));
        registry.add("external.api.key", () -> "integration-test-key");
    }

    @Test
    public void getTestNumericalQuestionReturns200() throws Exception {
        mockWebServer.enqueue(new MockResponse()
            .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .setBody("""
                {"question":"How many regions does Czechia have?","correctAnswer":"14"}
                """));

        mockMvc.perform(get("/api/test-numerical-question"))
            .andExpect(status().isOk())
            .andExpect(content().json("""
                {
                  "question":"How many regions does Czechia have?",
                  "correctAnswer":"14"
                }
                """));
    }

    @Test
    public void getTestNumericalQuestionReturns404WhenUpstreamNotFound() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        mockMvc.perform(get("/api/test-numerical-question"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Requested resource was not found in external API."));
    }

    @Test
    public void getTestNumericalQuestionReturns401WhenUpstreamUnauthorized() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(401));

        mockMvc.perform(get("/api/test-numerical-question"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("External API authentication failed."));
    }

    @TestConfiguration
    static class WebClientTestConfig {
        @Bean
        WebClient.Builder webClientBuilder() {
            return WebClient.builder();
        }
    }
}
