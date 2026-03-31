package cz.scrumdojo.quizmaster.question;

import cz.scrumdojo.quizmaster.IdResponse;
import cz.scrumdojo.quizmaster.ResponseHelper;
import cz.scrumdojo.quizmaster.workspace.WorkspaceRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workspaces/{guid}/questions")
public class QuestionMakeController {

    private final WorkspaceRepository workspaceRepository;
    private final QuestionRepository questionRepository;

    public QuestionMakeController(WorkspaceRepository workspaceRepository, QuestionRepository questionRepository) {
        this.workspaceRepository = workspaceRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getWorkspaceQuestion(@PathVariable String guid, @PathVariable Integer id) {
        if (!workspaceRepository.existsById(guid))
            return ResponseEntity.notFound().build();

        return ResponseHelper.okOrNotFound(questionRepository.findByIdAndWorkspaceGuid(id, guid).map(QuestionResponse::from));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<IdResponse> createWorkspaceQuestion(
            @PathVariable String guid, @Valid @RequestBody QuestionRequest request) {
        if (!workspaceRepository.existsById(guid))
            return ResponseEntity.notFound().build();

        var created = questionRepository.save(request.toEntity(guid));
        return ResponseEntity.ok(new IdResponse(created.getId()));
    }

    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<IdResponse> updateWorkspaceQuestion(
            @PathVariable String guid, @PathVariable Integer id, @Valid @RequestBody QuestionRequest request) {
        if (!workspaceRepository.existsById(guid))
            return ResponseEntity.notFound().build();

        return questionRepository.findByIdAndWorkspaceGuid(id, guid)
            .map(existing -> {
                var question = request.toEntity(guid);
                question.setId(existing.getId());
                questionRepository.save(question);
                return ResponseEntity.ok(new IdResponse(existing.getId()));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @Transactional
    @DeleteMapping("/{id}")
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
}
