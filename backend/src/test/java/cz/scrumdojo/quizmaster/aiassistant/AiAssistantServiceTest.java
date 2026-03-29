package cz.scrumdojo.quizmaster.aiassistant;

import org.junit.jupiter.api.Tag;
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

    @Tag("ai")
    @Test
    void generateQuestionReturnsValidResponse_single() {
        assumeTrue(!apiToken.isBlank(), "ai.token not configured");

        var response = aiAssistantService.generateQuestion(AiAssistantQuestionType.SINGLE, "capital cities of Europe");

        assertNotNull(response.question());
        assertEquals(AiAssistantQuestionType.SINGLE, response.type());
        assertFalse(response.question().isBlank());
        assertNotNull(response.answers());
        assertTrue(response.answers().length >= 2);
        assertNotNull(response.correctAnswers());
        assertEquals(1, response.correctAnswers().length);
        assertEquals(0, response.correctAnswers()[0]);
    }

    @Tag("ai")
    @Test
    void generateQuestionReturnsValidResponse_multiple() {
        assumeTrue(!apiToken.isBlank(), "ai.token not configured");

        var response = aiAssistantService.generateQuestion(AiAssistantQuestionType.MULTIPLE, "capital cities of Europe");

        assertNotNull(response.question());
        assertEquals(AiAssistantQuestionType.MULTIPLE, response.type());
        assertFalse(response.question().isBlank());
        assertNotNull(response.answers());
        assertTrue(response.answers().length >= 2);
        assertNotNull(response.correctAnswers());
        assertTrue(response.correctAnswers().length >= 2);
        assertEquals(0, response.correctAnswers()[0]);
    }

    @Test
    void generateQuestionFailsOnEmptyPrompt() {
        assertThrows(ResponseStatusException.class, () -> aiAssistantService.generateQuestion(AiAssistantQuestionType.SINGLE, "   "));
    }

    @Test
    void cleanAiResponseTest() {
        String cleaned = aiAssistantService.cleanAiResponse("""
                ```json
                {
                    "question": "Which dog breed is typically recognized as the tallest?",
                    "answers": ["Great Dane", "German Shepherd", "Golden Retriever", "Labrador Retriever"],
                    "correctAnswers": [0]
                }
                ```
                """);

        assertEquals("""
                {
                    "question": "Which dog breed is typically recognized as the tallest?",
                    "answers": ["Great Dane", "German Shepherd", "Golden Retriever", "Labrador Retriever"],
                    "correctAnswers": [0]
                }""", cleaned);
    }

    @Test
    void cleanAiResponseTest_wrongInput() {
        assertThrows(ResponseStatusException.class, () -> {
            aiAssistantService.cleanAiResponse("I'm Sorry Dave, I'm Afraid I Can't Do That");
        });
    }

    @Test
    void cleanAiResponseTest_empty() {
        assertThrows(ResponseStatusException.class, () -> {
            aiAssistantService.cleanAiResponse("");
        });
    }

    @Test
    void cleanAiResponseTest_null() {
        assertThrows(ResponseStatusException.class, () -> {
            aiAssistantService.cleanAiResponse(null);
        });
    }
}
