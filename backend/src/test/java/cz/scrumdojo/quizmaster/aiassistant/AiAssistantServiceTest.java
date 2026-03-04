package cz.scrumdojo.quizmaster.aiassistant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
public class AiAssistantServiceTest {

    @Autowired
    private AiAssistantService aiAssistantService;

    @Value("${ai.token:}")
    private String apiToken;

    @Test
    void generateQuestionReturnsValidResponse() {
        assumeTrue(!apiToken.isBlank(), "ai.token not configured");

        var response = aiAssistantService.generateQuestion("capital cities of Europe");

        assertNotNull(response.question());
        assertFalse(response.question().isBlank());
        assertNotNull(response.answers());
        assertEquals(2, response.answers().length);
        assertNotNull(response.correctAnswers());
        assertEquals(1, response.correctAnswers().length);
        assertEquals(0, response.correctAnswers()[0]);
    }

    @Test
    void generateQuestionFailsOnEmptyPrompt() {
        assertThrows(ResponseStatusException.class, () -> aiAssistantService.generateQuestion("   "));
    }
}
