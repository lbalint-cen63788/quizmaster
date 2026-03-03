package cz.scrumdojo.quizmaster.workspace;

import cz.scrumdojo.quizmaster.TestFixtures;
import cz.scrumdojo.quizmaster.question.Question;
import cz.scrumdojo.quizmaster.quiz.Quiz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WorkspaceControllerTest {

    @Autowired
    private WorkspaceController workspaceController;

    @Autowired
    private TestFixtures fixtures;

    @Test
    public void saveAndGetWorkspace() {
        var response = workspaceController.saveWorkspace(fixtures.workspace().title("Test Workspace").build()).getBody();
        assertNotNull(response);
        assertNotNull(response.guid());

        Workspace workspace = workspaceController.getWorkspace(response.guid()).getBody();
        assertNotNull(workspace);
        assertEquals("Test Workspace", workspace.getTitle());
    }

    @Test
    public void getWorkspaceQuestions() {
        Workspace workspace = fixtures.save(fixtures.workspace());
        Question question1 = fixtures.save(fixtures.questionIn(workspace));
        Question question2 = fixtures.save(fixtures.questionIn(workspace));
        Quiz quiz = fixtures.quiz(question2).build();
        fixtures.save(quiz);

        List<QuestionListItem> result = workspaceController.getWorkspaceQuestions(workspace.getGuid());

        assertEquals(2, result.size());
        assertEquals(question1.getId(), result.get(0).id());
        assertEquals(question2.getId(), result.get(1).id());
        assertFalse(result.get(0).isInAnyQuiz());
        assertTrue(result.get(1).isInAnyQuiz());
    }

    @Test
    public void getWorkspaceQuizzes() {
        Workspace workspace = fixtures.save(fixtures.workspace());

        Quiz quiz1 = fixtures.save(fixtures.quizIn(workspace));
        Quiz quiz2 = fixtures.save(fixtures.quizIn(workspace));
        fixtures.save(fixtures.quiz()); // Quiz without workspace

        List<QuizListItem> quizzes = workspaceController.getWorkspaceQuizzes(workspace.getGuid());

        assertEquals(2, quizzes.size());
        assertEquals(quiz1.getId(), quizzes.get(0).id());
        assertEquals(quiz2.getId(), quizzes.get(1).id());
    }
}
