package cz.scrumdojo.quizmaster.quiz;

import cz.scrumdojo.quizmaster.TestFixtures;
import cz.scrumdojo.quizmaster.question.Question;
import cz.scrumdojo.quizmaster.workspace.Workspace;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class QuizMakeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestFixtures fixtures;

    @Test
    public void createQuizInWorkspace() throws Exception {
        Workspace workspace = fixtures.save(fixtures.workspace());
        Question question = fixtures.save(fixtures.questionIn(workspace));

        mockMvc.perform(post("/api/workspaces/{guid}/quizzes", workspace.getGuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "New Quiz",
                        "description": "A quiz",
                        "questionIds": [%d],
                        "mode": "learn",
                        "passScore": 80,
                        "workspaceGuid": "%s",
                        "randomQuestionCount": 1
                    }
                    """.formatted(question.getId(), workspace.getGuid())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    public void updateQuizInWorkspace() throws Exception {
        Workspace workspace = fixtures.save(fixtures.workspace());
        Question question = fixtures.save(fixtures.questionIn(workspace));
        Quiz quiz = fixtures.save(fixtures.quiz(question).workspaceGuid(workspace.getGuid()).build());

        mockMvc.perform(put("/api/workspaces/{guid}/quizzes/{id}", workspace.getGuid(), quiz.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "Updated Quiz",
                        "description": "Updated description",
                        "questionIds": [%d],
                        "mode": "exam",
                        "passScore": 90,
                        "workspaceGuid": "%s",
                        "randomQuestionCount": 1
                    }
                    """.formatted(question.getId(), workspace.getGuid())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(quiz.getId()));

        mockMvc.perform(get("/api/quiz/{id}", quiz.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated Quiz"));
    }

    @Test
    public void updateQuizInWrongWorkspaceReturns404() throws Exception {
        Workspace workspace1 = fixtures.save(fixtures.workspace());
        Workspace workspace2 = fixtures.save(fixtures.workspace());
        Question question = fixtures.save(fixtures.questionIn(workspace1));
        Quiz quiz = fixtures.save(fixtures.quiz(question).workspaceGuid(workspace1.getGuid()).build());

        mockMvc.perform(put("/api/workspaces/{guid}/quizzes/{id}", workspace2.getGuid(), quiz.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "Updated Quiz",
                        "description": "Updated",
                        "questionIds": [%d],
                        "mode": "exam",
                        "passScore": 90,
                        "workspaceGuid": "%s",
                        "randomQuestionCount": 1
                    }
                    """.formatted(question.getId(), workspace2.getGuid())))
            .andExpect(status().isNotFound());
    }

    @Test
    public void createQuizInNonExistentWorkspaceReturns404() throws Exception {
        mockMvc.perform(post("/api/workspaces/{guid}/quizzes", "non-existent-guid")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "Quiz",
                        "description": "A quiz",
                        "questionIds": [],
                        "mode": "learn",
                        "passScore": 80,
                        "randomQuestionCount": 0
                    }
                    """))
            .andExpect(status().isNotFound());
    }
}
