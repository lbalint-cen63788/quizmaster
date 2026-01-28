package cz.scrumdojo.quizmaster.question;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestNumericalQuestionController {

    @GetMapping("/test-numerical-question")
    public TestNumericalQuestionResponse getTestNumericalQuestion() {
        return new TestNumericalQuestionResponse(
            "How many regions does Czechia have?",
            "14"
        );
    }
}
