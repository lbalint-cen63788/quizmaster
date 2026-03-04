package cz.scrumdojo.quizmaster.aiassistant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiAssistantController.class)
public class AiAssistantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiAssistantService aiAssistantService;

    @Test
    public void generateAnswer() throws Exception {
        when(aiAssistantService.generateQuestion("Vygeneruj otázku do quizu"))
            .thenReturn("AI generated question");

        mockMvc.perform(post("/api/ai-assistant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"question": "Vygeneruj otázku do quizu"}
                    """))
            .andExpect(status().isOk())
            .andExpect(content().json("""
                {"answer": "AI generated question"}
                """));
    }

    @Test
    public void emptyInputReturnsBadRequest() throws Exception {
        when(aiAssistantService.generateQuestion("   "))
            .thenThrow(new AiAssistantException(HttpStatus.BAD_REQUEST, "Question must not be empty."));

        mockMvc.perform(post("/api/ai-assistant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"question": "   "}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Question must not be empty."));
    }

    @Test
    public void rateLimitReturnsTooManyRequests() throws Exception {
        when(aiAssistantService.generateQuestion(anyString()))
            .thenThrow(new AiAssistantException(HttpStatus.TOO_MANY_REQUESTS, "AI assistant rate limit reached."));

        mockMvc.perform(post("/api/ai-assistant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"question": "Prompt"}
                    """))
            .andExpect(status().isTooManyRequests())
            .andExpect(jsonPath("$.message").value("AI assistant rate limit reached."));
    }

    @Test
    public void invalidTokenReturnsUnauthorized() throws Exception {
        when(aiAssistantService.generateQuestion(anyString()))
            .thenThrow(new AiAssistantException(HttpStatus.UNAUTHORIZED, "AI token is invalid."));

        mockMvc.perform(post("/api/ai-assistant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"question": "Prompt"}
                    """))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("AI token is invalid."));
    }

    @Test
    public void upstreamErrorReturnsBadGateway() throws Exception {
        when(aiAssistantService.generateQuestion(anyString()))
            .thenThrow(new AiAssistantException(HttpStatus.BAD_GATEWAY, "Failed to generate AI response."));

        mockMvc.perform(post("/api/ai-assistant")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"question": "Prompt"}
                    """))
            .andExpect(status().isBadGateway())
            .andExpect(jsonPath("$.message").value("Failed to generate AI response."));
    }
}
