package cz.scrumdojo.quizmaster.attempt;

import cz.scrumdojo.quizmaster.ResponseHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attempt")
public class AttemptController {

    private final AttemptRepository attemptRepository;

    public AttemptController(AttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    @Transactional(readOnly = true)
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<AttemptResponse>> getAttemptsByQuiz(@PathVariable Integer quizId) {
        List<AttemptResponse> attempts = attemptRepository.findByQuizIdOrderByStartedAtDesc(quizId)
                .stream()
                .map(AttemptResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(attempts);
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<AttemptResponse> getAttempt(@PathVariable Integer id) {
        return ResponseHelper.okOrNotFound(
                attemptRepository.findById(id).map(AttemptResponse::fromEntity)
        );
    }

    @Transactional
    @PostMapping
    public ResponseEntity<AttemptResponse> createAttempt(@RequestBody AttemptRequest request) {
        Attempt attempt = attemptRepository.save(request.toEntity());
        return ResponseEntity.ok(AttemptResponse.fromEntity(attempt));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<AttemptResponse> updateAttempt(@PathVariable Integer id, @RequestBody AttemptRequest request) {
        Attempt attempt = request.toEntity();
        attempt.setId(id);
        Attempt saved = attemptRepository.save(attempt);
        return ResponseEntity.ok(AttemptResponse.fromEntity(saved));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttempt(@PathVariable Integer id) {
        if (!attemptRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        attemptRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

