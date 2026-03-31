package cz.scrumdojo.quizmaster.quiz;

import cz.scrumdojo.quizmaster.IdResponse;
import cz.scrumdojo.quizmaster.workspace.WorkspaceRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workspaces/{guid}/quizzes")
public class QuizMakeController {

    private final WorkspaceRepository workspaceRepository;
    private final QuizRepository quizRepository;

    public QuizMakeController(WorkspaceRepository workspaceRepository, QuizRepository quizRepository) {
        this.workspaceRepository = workspaceRepository;
        this.quizRepository = quizRepository;
    }

    @PostMapping
    public ResponseEntity<IdResponse> createQuiz(
            @PathVariable String guid, @Valid @RequestBody QuizRequest request) {
        if (!workspaceRepository.existsById(guid))
            return ResponseEntity.notFound().build();

        Quiz output = quizRepository.save(request.toEntity());
        return ResponseEntity.ok(new IdResponse(output.getId()));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<IdResponse> updateQuiz(
            @PathVariable String guid, @PathVariable Integer id, @Valid @RequestBody QuizRequest request) {
        if (!workspaceRepository.existsById(guid))
            return ResponseEntity.notFound().build();

        return quizRepository.findByIdAndWorkspaceGuid(id, guid)
            .map(existing -> {
                Quiz quiz = request.toEntity();
                quiz.setId(existing.getId());
                quizRepository.save(quiz);
                return ResponseEntity.ok(new IdResponse(existing.getId()));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
