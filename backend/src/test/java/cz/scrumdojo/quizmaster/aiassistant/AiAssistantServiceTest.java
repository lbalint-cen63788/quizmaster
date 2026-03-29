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
    void generateSingleChoiceQuestion() {
        assumeTrue(!apiToken.isBlank(), "ai.token not configured");

        var response = aiAssistantService.generateQuestion(
            AiAssistantQuestionType.SINGLE,
            "Generate a question about capital cities of Europe with 1 correct answer and 3 incorrect answers"
        );

        assertNotNull(response.question());
        assertFalse(response.question().isBlank());
        assertEquals(AiAssistantQuestionType.SINGLE, response.type());
        assertEquals(4, response.answers().length);
        assertEquals(1, response.correctAnswers().length);
    }

    @Tag("ai")
    @Test
    void generateMultipleChoiceQuestion() {
        assumeTrue(!apiToken.isBlank(), "ai.token not configured");

        var response = aiAssistantService.generateQuestion(
            AiAssistantQuestionType.MULTIPLE,
            "Generate a question about European capitals with 2 correct answers and 2 incorrect answers"
        );

        assertNotNull(response.question());
        assertFalse(response.question().isBlank());
        assertEquals(AiAssistantQuestionType.MULTIPLE, response.type());
        assertEquals(4, response.answers().length);
        assertEquals(2, response.correctAnswers().length);
    }

    @Test
    void generateQuestionFailsOnEmptyPrompt() {
        assertThrows(ResponseStatusException.class, () -> aiAssistantService.generateQuestion(AiAssistantQuestionType.SINGLE, "   "));
    }

    @Tag("ai")
    @Test
    void generateQuestionWithSpecificAnswerCount() {
        assumeTrue(!apiToken.isBlank(), "ai.token not configured");

        var response = aiAssistantService.generateQuestion(
            AiAssistantQuestionType.SINGLE,
            "Create a multiple-choice question on exoplanets with exactly 2 correct answers out of 5 total answers"
        );

        assertFalse(response.question().isBlank());
        assertEquals(5, response.answers().length);
        assertEquals(2, response.correctAnswers().length);
    }

    @Tag("ai")
    @Test
    void generateQuestionWithAnswerRange() {
        assumeTrue(!apiToken.isBlank(), "ai.token not configured");

        var response = aiAssistantService.generateQuestion(
            AiAssistantQuestionType.SINGLE,
            "Create a single-choice question about European capitals with exactly 3 answers and 1 correct answer"
        );

        assertFalse(response.question().isBlank());
        assertEquals(3, response.answers().length);
        assertEquals(1, response.correctAnswers().length);
    }

    @Test
    void validateResponse_valid() {
        assertDoesNotThrow(() -> AiAssistantService.validateResponse(
            new AiAssistantService.AssistantResponse("What is 2+2?", new String[]{"4", "5"}, new int[]{0})
        ));
    }

    @Test
    void validateResponse_emptyQuestion() {
        assertThrows(ResponseStatusException.class, () -> AiAssistantService.validateResponse(
            new AiAssistantService.AssistantResponse("", new String[]{"4", "5"}, new int[]{0})
        ));
    }

    @Test
    void validateResponse_nullQuestion() {
        assertThrows(ResponseStatusException.class, () -> AiAssistantService.validateResponse(
            new AiAssistantService.AssistantResponse(null, new String[]{"4", "5"}, new int[]{0})
        ));
    }

    @Test
    void validateResponse_tooFewAnswers() {
        assertThrows(ResponseStatusException.class, () -> AiAssistantService.validateResponse(
            new AiAssistantService.AssistantResponse("Question?", new String[]{"only one"}, new int[]{0})
        ));
    }

    @Test
    void validateResponse_noCorrectAnswers() {
        assertThrows(ResponseStatusException.class, () -> AiAssistantService.validateResponse(
            new AiAssistantService.AssistantResponse("Question?", new String[]{"a", "b"}, new int[]{})
        ));
    }

    @Test
    void validateResponse_indexOutOfBounds() {
        assertThrows(ResponseStatusException.class, () -> AiAssistantService.validateResponse(
            new AiAssistantService.AssistantResponse("Question?", new String[]{"a", "b"}, new int[]{5})
        ));
    }

    @Test
    void validateResponse_negativeIndex() {
        assertThrows(ResponseStatusException.class, () -> AiAssistantService.validateResponse(
            new AiAssistantService.AssistantResponse("Question?", new String[]{"a", "b"}, new int[]{-1})
        ));
    }
}
