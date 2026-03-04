package cz.scrumdojo.quizmaster.workspace;

import cz.scrumdojo.quizmaster.question.Question;
import cz.scrumdojo.quizmaster.question.QuestionRepository;
import cz.scrumdojo.quizmaster.quiz.Quiz;
import cz.scrumdojo.quizmaster.quiz.QuizRepository;
import cz.scrumdojo.quizmaster.ResponseHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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
    public ResponseEntity<WorkspaceCreateResponse> saveWorkspace(@RequestBody WorkspaceRequest request) {
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
            .map(q -> new QuestionListItem(q.getId(), q.getQuestion(), q.getEditId(), questionIdsInQuizzes.contains(q.getId())))
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
}
