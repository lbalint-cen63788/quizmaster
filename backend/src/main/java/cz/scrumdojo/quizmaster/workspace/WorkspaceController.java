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
    public ResponseEntity<Workspace> getWorkspace(@PathVariable String guid) {
        return ResponseHelper.okOrNotFound(workspaceRepository.findById(guid));
    }

    @PostMapping
    public ResponseEntity<WorkspaceCreateResponse> saveWorkspace(@RequestBody Workspace workspace) {
        var createdWorkspace = workspaceRepository.save(workspace);
        return ResponseEntity.ok(new WorkspaceCreateResponse(createdWorkspace.getGuid()));
    }

    @Transactional(readOnly = true)
    @GetMapping("/{guid}/questions")
    public List<QuestionListItem> getWorkspaceQuestions(@PathVariable String guid) {
        List<Question> questions = questionRepository.findByWorkspaceGuid(guid);
        Set<Integer> questionIdsInQuizzes = quizRepository.findQuestionIdsInQuizzesByWorkspaceGuid(guid);

        return questions.stream()
            .map(q -> new QuestionListItem(q.getId(), q.getQuestion(), q.getEditId(), questionIdsInQuizzes.contains(q.getId())))
            .toList();
    }

    @Transactional(readOnly = true)
    @GetMapping("/{guid}/quizzes")
    public List<QuizListItem> getWorkspaceQuizzes(@PathVariable String guid) {
        List<Quiz> quizzes = quizRepository.findByWorkspaceGuid(guid);

        return quizzes.stream()
            .map(quiz -> new QuizListItem(quiz.getId(), quiz.getTitle()))
            .toList();
    }
}
