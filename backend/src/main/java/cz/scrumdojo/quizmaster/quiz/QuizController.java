package cz.scrumdojo.quizmaster.quiz;

import cz.scrumdojo.quizmaster.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;
    private final QuizRepository quizRepository;

    @Autowired
    public QuizController(QuizService quizService, QuizRepository quizRepository) {
        this.quizService = quizService;
        this.quizRepository = quizRepository;
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<QuizResponse> getQuiz(@PathVariable Integer id) {
        return ResponseHelper.okOrNotFound(quizService.getQuiz(id));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<Integer> createQuiz(@RequestBody Quiz quizInput) {
        Quiz output = quizRepository.save(quizInput);
        return ResponseEntity.ok(output.getId());
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateQuiz(@PathVariable Integer id, @RequestBody Quiz quizInput) {
        quizInput.setId(id);
        Quiz output = quizRepository.save(quizInput);
        return ResponseEntity.ok(output.getId());
    }
}
