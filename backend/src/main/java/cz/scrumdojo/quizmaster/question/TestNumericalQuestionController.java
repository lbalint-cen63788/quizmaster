package cz.scrumdojo.quizmaster.question;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestNumericalQuestionController {

    private static final String QUESTION_SLUG = "test-numerical-question";

    private final QuestionRepository questionRepository;

    @GetMapping("/test-numerical-question")
    public TestNumericalQuestionResponse getTestNumericalQuestion() {
        var question = questionRepository
            .findBySlug(QUESTION_SLUG)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test numerical question not found"));

        var answers = question.getAnswers();
        var correctAnswers = question.getCorrectAnswers();

        if (answers == null || correctAnswers == null || correctAnswers.length != 1) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Test numerical question must have exactly one correct answer");
        }

        var answerIdx = correctAnswers[0];
        if (answerIdx < 0 || answerIdx >= answers.length) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Correct answer index is out of bounds");
        }

        return new TestNumericalQuestionResponse(question.getQuestion(), answers[answerIdx]);
    }
}
