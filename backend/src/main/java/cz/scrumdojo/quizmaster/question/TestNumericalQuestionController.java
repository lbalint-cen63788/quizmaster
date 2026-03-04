package cz.scrumdojo.quizmaster.question;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestNumericalQuestionController {

    private final TestNumericalQuestionService service;

    public TestNumericalQuestionController(TestNumericalQuestionService service) {
        this.service = service;
    }

    @GetMapping("/test-numerical-question")
    public ResponseEntity<?> getTestNumericalQuestion() {
        try {
            return ResponseEntity.ok(service.getTestNumericalQuestion());
        } catch (ExternalApiException e) {
            return ResponseEntity.status(e.getStatus())
                .body(new ExternalApiErrorResponse(e.getMessage()));
        }
    }
}
