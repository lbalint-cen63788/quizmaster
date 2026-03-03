package cz.scrumdojo.quizmaster.question;

import cz.scrumdojo.quizmaster.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestion(@PathVariable Integer id) {
        return ResponseHelper.okOrNotFound(questionRepository.findById(id));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Integer id) {
        questionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Transactional(readOnly = true)
    @GetMapping("/{editId}/edit")
    public ResponseEntity<Question> getQuestionByEditId(@PathVariable String editId) {
        return ResponseHelper.okOrNotFound(questionRepository.findByEditId(editId));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<QuestionCreateResponse> saveQuestion(@RequestBody Question question) {
        var createdQuestion = questionRepository.save(question);
        return ResponseEntity.ok(new QuestionCreateResponse(createdQuestion.getId(), createdQuestion.getEditId()));
    }

    @Transactional
    @PatchMapping("/{editId}")
    public ResponseEntity<QuestionCreateResponse> updateQuestion(@RequestBody Question question, @PathVariable String editId) {
        return questionRepository.findByEditId(editId)
            .map(existing -> {
                question.setId(existing.getId());
                question.setEditId(editId);
                questionRepository.save(question);
                return ResponseEntity.ok(new QuestionCreateResponse(existing.getId(), editId));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
