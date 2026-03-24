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
    public void saveAndGetQuestion() {
        var request = fixtures.questionRequest();
        var response = questionController.saveQuestion(request).getBody();
        assertNotNull(response);
        assertNotNull(response.id());

        Question result = questionController.getQuestion(response.id()).getBody();
        assertNotNull(result);
        assertEquals(request.question(), result.getQuestion());
        assertArrayEquals(request.answers(), result.getAnswers());
        assertArrayEquals(request.correctAnswers(), result.getCorrectAnswers());
        assertArrayEquals(request.explanations(), result.getExplanations());
        assertEquals(request.easyMode(), result.isEasyMode());
    }

    @Test
    public void deleteQuestion() {
        var request = fixtures.questionRequest();
        var response = questionController.saveQuestion(request).getBody();
        assertNotNull(response);
        var id = response.id();
        questionController.deleteQuestion(id);
        var result = questionController.getQuestion(id).getBody();
        assertNull(result);
    }

    @Test
    public void updateQuestion() {
        var originalQuestion = fixtures.save(fixtures.question());

        var updateRequest = fixtures.multipleChoiceQuestionRequest();
        questionController.updateQuestion(updateRequest, originalQuestion.getEditId());

        var result = questionController.getQuestion(originalQuestion.getId()).getBody();
        assertNotNull(result);
        assertEquals(updateRequest.question(), result.getQuestion());
        assertArrayEquals(updateRequest.answers(), result.getAnswers());
        assertArrayEquals(updateRequest.correctAnswers(), result.getCorrectAnswers());
        assertArrayEquals(updateRequest.explanations(), result.getExplanations());
        assertEquals(updateRequest.easyMode(), result.isEasyMode());
    }

    @Test
    public void getQuestionByEditId() {
        var question = fixtures.save(fixtures.question());

        Question result = questionController.getQuestionByEditId(question.getEditId()).getBody();

        assertNotNull(result);
        assertEquals(question.getId(), result.getId());
    }

    @Test
    public void saveQuestion() {
        var request = fixtures.questionRequest();
        var response = questionController.saveQuestion(request).getBody();
        assertNotNull(response);

        assertNotNull(response.id());
        assertNotNull(response.editId());
    }

    @Test
    public void updateNonExistentQuestionReturns404() {
        var request = fixtures.questionRequest();
        var response = questionController.updateQuestion(request, "non-existent-edit-id");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void saveAndGetQuestionWithImageUrl() {
        var imageUrl = "https://placekitten.com/300/200";
        var request = fixtures.questionRequestWithImage(imageUrl);
        var response = questionController.saveQuestion(request).getBody();
        assertNotNull(response);

        Question result = questionController.getQuestion(response.id()).getBody();
        assertNotNull(result);
        assertEquals(imageUrl, result.getImageUrl());
    }

    @Test
    public void nonExistingQuestion() {
        ResponseEntity<?> response = questionController.getQuestion(-1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void saveAiGeneratedQuestionValidSingleChoice() {
        var request = new QuestionRequest(
            "What is the capital of Czech Republic?",
            new String[]{"Prague", "Brno"},
            new int[]{0},
            new String[]{"", ""},
            null,
            false,
            null,
            null,
            true,
            "single",
            null
        );

        var response = questionController.saveQuestion(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void saveAiGeneratedQuestionInvalidWhenTwoCorrectAnswers() {
        var request = new QuestionRequest(
            "What is the capital of Czech Republic?",
            new String[]{"Prague", "Brno"},
            new int[]{0, 1},
            new String[]{"", ""},
            null,
            false,
            null,
            null,
            true,
            "single",
            null
        );

        var response = questionController.saveQuestion(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
