package cz.scrumdojo.quizmaster.workspace;

import cz.scrumdojo.quizmaster.question.Question;
import cz.scrumdojo.quizmaster.question.QuestionRepository;
import cz.scrumdojo.quizmaster.question.QuestionRequest;
import cz.scrumdojo.quizmaster.question.QuestionWriteResponse;
import cz.scrumdojo.quizmaster.quiz.Quiz;
import cz.scrumdojo.quizmaster.quiz.QuizRepository;
import cz.scrumdojo.quizmaster.ResponseHelper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceRepository workspaceRepository;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    public WorkspaceController(
        WorkspaceRepository workspaceRepository,
        QuestionRepository questionRepository,
        QuizRepository quizRepository) {
        this.workspaceRepository = workspaceRepository;
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
    }

    @GetMapping("/{guid}")
    public ResponseEntity<WorkspaceResponse> getWorkspace(@PathVariable String guid) {
        return ResponseHelper.okOrNotFound(
            workspaceRepository.findById(guid).map(WorkspaceResponse::from));
    }

    @PostMapping
    public ResponseEntity<WorkspaceCreateResponse> saveWorkspace(@Valid @RequestBody WorkspaceRequest request) {
        var createdWorkspace = workspaceRepository.save(request.toEntity());
        return ResponseEntity.ok(new WorkspaceCreateResponse(createdWorkspace.getGuid()));
    }

    @Transactional(readOnly = true)
    @GetMapping("/{guid}/questions")
    public ResponseEntity<List<QuestionListItem>> getWorkspaceQuestions(@PathVariable String guid) {
        if (!workspaceRepository.existsById(guid))
            return ResponseEntity.notFound().build();

        List<Question> questions = questionRepository.findByWorkspaceGuid(guid);
        Set<Integer> questionIdsInQuizzes = quizRepository.findQuestionIdsInQuizzesByWorkspaceGuid(guid);

        var items = questions.stream()
            .map(q -> new QuestionListItem(q.getId(), q.getQuestion(), q.getEditId(), questionIdsInQuizzes.contains(q.getId()), q.getImageUrl()))
            .toList();

        return ResponseEntity.ok(items);
    }

    @Transactional(readOnly = true)
    @GetMapping("/{guid}/quizzes")
    public ResponseEntity<List<QuizListItem>> getWorkspaceQuizzes(@PathVariable String guid) {
        if (!workspaceRepository.existsById(guid))
            return ResponseEntity.notFound().build();

        List<Quiz> quizzes = quizRepository.findByWorkspaceGuid(guid);

        var items = quizzes.stream()
            .map(quiz -> new QuizListItem(quiz.getId(), quiz.getTitle()))
            .toList();

        return ResponseEntity.ok(items);
    }

    @Transactional(readOnly = true)
    @GetMapping("/{guid}/questions/{id}")
    public ResponseEntity<Question> getWorkspaceQuestion(@PathVariable String guid, @PathVariable Integer id) {
        if (!workspaceRepository.existsById(guid))
            return ResponseEntity.notFound().build();

        return ResponseHelper.okOrNotFound(questionRepository.findByIdAndWorkspaceGuid(id, guid));
    }

    @Transactional
    @PostMapping("/{guid}/questions")
    public ResponseEntity<QuestionWriteResponse> createWorkspaceQuestion(
            @PathVariable String guid, @RequestBody QuestionRequest request) {
        if (!workspaceRepository.existsById(guid))
            return ResponseEntity.notFound().build();

        var validationError = validateQuestionRequest(request);
        if (validationError != null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        var question = request.toEntity();
        question.setWorkspaceGuid(guid);
        var created = questionRepository.save(question);
        return ResponseEntity.ok(new QuestionWriteResponse(created.getId(), created.getEditId()));
    }

    @Transactional
    @PatchMapping("/{guid}/questions/{id}")
    public ResponseEntity<QuestionWriteResponse> updateWorkspaceQuestion(
            @PathVariable String guid, @PathVariable Integer id, @RequestBody QuestionRequest request) {
        if (!workspaceRepository.existsById(guid))
            return ResponseEntity.notFound().build();

        var validationError = validateQuestionRequest(request);
        if (validationError != null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return questionRepository.findByIdAndWorkspaceGuid(id, guid)
            .map(existing -> {
                var question = request.toEntity();
                question.setId(existing.getId());
                question.setEditId(existing.getEditId());
                question.setWorkspaceGuid(guid);
                questionRepository.save(question);
                return ResponseEntity.ok(new QuestionWriteResponse(existing.getId(), existing.getEditId()));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @Transactional
    @DeleteMapping("/{guid}/questions/{id}")
    public ResponseEntity<Void> deleteWorkspaceQuestion(@PathVariable String guid, @PathVariable Integer id) {
        if (!workspaceRepository.existsById(guid))
            return ResponseEntity.notFound().build();

        return questionRepository.findByIdAndWorkspaceGuid(id, guid)
            .map(existing -> {
                questionRepository.deleteById(id);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }

    private static final Pattern HTTP_URL_REGEX = Pattern.compile("^https?://\\S+$", Pattern.CASE_INSENSITIVE);
    private static final Pattern IMAGE_FILE_URL_REGEX = Pattern.compile("\\.(png|jpe?g|gif|webp|bmp|svg)(?:$|[?#])", Pattern.CASE_INSENSITIVE);

    private String validateQuestionRequest(QuestionRequest request) {
        if (request == null || request.question() == null || request.question().trim().isEmpty())
            return "question";

        if (!isValidImageUrl(request.imageUrl()))
            return "imageUrl";

        if (request.imageUrl() != null && request.imageUrl().trim().length() > 2048)
            return "imageUrl";

        if (!Boolean.TRUE.equals(request.aiGenerated()))
            return null;

        var normalizedType = request.questionType() == null ? "" : request.questionType().trim().toLowerCase();
        if (request.answers() == null || request.answers().length < 2)
            return "answers";

        if (request.correctAnswers() == null
            || ("multiple".equals(normalizedType) && request.correctAnswers().length < 2)
            || ("single".equals(normalizedType) && request.correctAnswers().length != 1)
        )
            return "correctAnswers";

        var first = request.answers()[0] == null ? "" : request.answers()[0].trim();
        var second = request.answers()[1] == null ? "" : request.answers()[1].trim();
        if (first.isEmpty() || second.isEmpty())
            return "answers";

        return null;
    }

    private boolean isValidImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty())
            return true;

        var normalized = imageUrl.trim();
        return HTTP_URL_REGEX.matcher(normalized).matches()
            && IMAGE_FILE_URL_REGEX.matcher(normalized).find();
    }
}
