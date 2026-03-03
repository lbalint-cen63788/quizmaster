package cz.scrumdojo.quizmaster.quiz;

import cz.scrumdojo.quizmaster.question.Question;
import cz.scrumdojo.quizmaster.question.QuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    @Autowired
    public QuizController(QuestionRepository questionRepository,
                          QuizRepository quizRepository) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<QuizResponse> getQuiz(@PathVariable Integer id) {
        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz == null) {
            return ResponseEntity.notFound().build();
        }

        int questionsLimit = (quiz.getSize() != null && quiz.getSize() > 0)
                ? quiz.getSize()
                : quiz.getQuestionIds().length;

        List<Question> questions = new ArrayList<>(questionsLimit);
        for (int i = 0; i < questionsLimit; i++) {
            questionRepository.findById(quiz.getQuestionIds()[i]).ifPresent(questions::add);
        }

        if (quiz.getFinalCount() != null && quiz.getFinalCount() > 0 && !questions.isEmpty()) {
            Collections.shuffle(questions);
            questions = new ArrayList<>(questions.subList(0, Math.min(quiz.getFinalCount(), questions.size())));
        }

        QuizResponse build = QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .questions(questions.toArray(new Question[0]))
                .mode(quiz.getMode())
                .difficulty(quiz.getDifficulty())
                .passScore(quiz.getPassScore())
                .timeLimit(quiz.getTimeLimit())
                .size(quiz.getSize())
                .build();

        return ResponseEntity.ok(build);
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
