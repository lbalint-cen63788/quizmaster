package cz.scrumdojo.quizmaster.question;

import cz.scrumdojo.quizmaster.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private static final Pattern HTTP_URL_REGEX = Pattern.compile("^https?://\\S+$", Pattern.CASE_INSENSITIVE);
    private static final Pattern IMAGE_FILE_URL_REGEX = Pattern.compile("\\.(png|jpe?g|gif|webp|bmp|svg)(?:$|[?#])", Pattern.CASE_INSENSITIVE);

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
    public ResponseEntity<QuestionWriteResponse> saveQuestion(@RequestBody QuestionRequest request) {
        var validationError = validateQuestionRequest(request);
        if (validationError != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        var createdQuestion = questionRepository.save(request.toEntity());
        return ResponseEntity.ok(new QuestionWriteResponse(createdQuestion.getId(), createdQuestion.getEditId()));
    }

    @Transactional
    @PatchMapping("/{editId}")
    public ResponseEntity<QuestionWriteResponse> updateQuestion(@RequestBody QuestionRequest request, @PathVariable String editId) {
        var validationError = validateQuestionRequest(request);
        if (validationError != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return questionRepository.findByEditId(editId)
            .map(existing -> {
                var question = request.toEntity();
                question.setId(existing.getId());
                question.setEditId(editId);
                questionRepository.save(question);
                return ResponseEntity.ok(new QuestionWriteResponse(existing.getId(), editId));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    private String validateQuestionRequest(QuestionRequest request) {
        if (request == null || request.question() == null || request.question().trim().isEmpty()) {
            return "question";
        }

        if (!isValidImageUrl(request.imageUrl())) {
            return "imageUrl";
        }

        if (request.imageUrl() != null && request.imageUrl().trim().length() > 2048) {
            return "imageUrl";
        }

        if (!Boolean.TRUE.equals(request.aiGenerated())) {
            return null;
        }

        var normalizedType = request.questionType() == null ? "" : request.questionType().trim().toLowerCase();
        if (request.answers() == null || request.answers().length < 2) {
            return "answers";
        }

        if (request.correctAnswers() == null
            || ("multiple".equals(normalizedType) && request.correctAnswers().length < 2)
            || ("single".equals(normalizedType) && request.correctAnswers().length != 1)
        ) {
            return "correctAnswers";
        }

        var first = request.answers()[0] == null ? "" : request.answers()[0].trim();
        var second = request.answers()[1] == null ? "" : request.answers()[1].trim();
        if (first.isEmpty() || second.isEmpty()) {
            return "answers";
        }

        return null;
    }

    private boolean isValidImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return true;
        }

        var normalized = imageUrl.trim();
        return HTTP_URL_REGEX.matcher(normalized).matches()
            && IMAGE_FILE_URL_REGEX.matcher(normalized).find();
    }
}
