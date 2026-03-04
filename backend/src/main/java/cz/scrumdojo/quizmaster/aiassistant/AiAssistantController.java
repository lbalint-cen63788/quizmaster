package cz.scrumdojo.quizmaster.aiassistant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai-assistant")
public class AiAssistantController {

    private final AiAssistantService aiAssistantService;

    public AiAssistantController(AiAssistantService aiAssistantService) {
        this.aiAssistantService = aiAssistantService;
    }

    @PostMapping
    public ResponseEntity<?> generate(@RequestBody AiAssistantRequest request) {
        String question = request == null ? null : request.question();

        try {
            AiAssistantResponse response = aiAssistantService.generateQuestion(question);
            return ResponseEntity.ok(response);
        } catch (AiAssistantException e) {
            return ResponseEntity.status(e.getStatus())
                .body(new AiAssistantErrorResponse(e.getMessage()));
        }
    }
}
