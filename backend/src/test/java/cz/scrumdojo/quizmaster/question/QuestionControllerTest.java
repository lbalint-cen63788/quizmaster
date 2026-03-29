package cz.scrumdojo.quizmaster.question;

import cz.scrumdojo.quizmaster.TestFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class QuestionControllerTest {

    @Autowired
    private TestFixtures fixtures;

    @Autowired
    private QuestionController questionController;

    @Test
    public void getQuestion() {
        var question = fixtures.save(fixtures.question());

        Question result = questionController.getQuestion(question.getId()).getBody();

        assertNotNull(result);
        assertEquals(question.getQuestion(), result.getQuestion());
        assertArrayEquals(question.getAnswers(), result.getAnswers());
        assertArrayEquals(question.getCorrectAnswers(), result.getCorrectAnswers());
    }

    @Test
    public void nonExistingQuestion() {
        ResponseEntity<?> response = questionController.getQuestion(-1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
