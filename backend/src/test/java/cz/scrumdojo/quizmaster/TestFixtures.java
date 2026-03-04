package cz.scrumdojo.quizmaster;

import cz.scrumdojo.quizmaster.question.Question;
import cz.scrumdojo.quizmaster.question.QuestionRepository;
import cz.scrumdojo.quizmaster.question.QuestionRequest;
import cz.scrumdojo.quizmaster.quiz.Quiz;
import cz.scrumdojo.quizmaster.quiz.QuizMode;
import cz.scrumdojo.quizmaster.quiz.Difficulty;
import cz.scrumdojo.quizmaster.quiz.QuizRepository;
import cz.scrumdojo.quizmaster.quiz.QuizRequest;
import cz.scrumdojo.quizmaster.workspace.Workspace;
import cz.scrumdojo.quizmaster.workspace.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class TestFixtures {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    public Question.QuestionBuilder question() {
        return Question.builder()
            .question("What is the capital of Italy?")
            .answers(new String[]{"Naples", "Rome", "Florence", "Palermo"})
            .correctAnswers(new int[]{1})
            .explanations(new String[]{"No", "Correct!", "No", "No"})
            .isEasyMode(false);
    }

    public Question.QuestionBuilder questionIn(Workspace workspace) {
        return question().workspaceGuid(workspace.getGuid());
    }

    public QuestionRequest questionRequest() {
        return new QuestionRequest(
            "What is the capital of Italy?",
            new String[]{"Naples", "Rome", "Florence", "Palermo"},
            new int[]{1},
            new String[]{"No", "Correct!", "No", "No"},
            null,
            false,
            null,
            null,
            false,
            "single"
        );
    }

    public QuestionRequest multipleChoiceQuestionRequest() {
        return new QuestionRequest(
            "Which are cities in Italy?",
            new String[]{"Naples", "Rome", "Paris", "Berlin"},
            new int[]{0, 1},
            new String[]{"Yes!", "Yes!", "No, France", "No, Germany"},
            null,
            false,
            null,
            null,
            false,
            "multiple"
        );
    }

    public QuestionRequest questionRequestWithImage(String imageUrl) {
        return new QuestionRequest(
            "What is the capital of Italy?",
            new String[]{"Naples", "Rome", "Florence", "Palermo"},
            new int[]{1},
            new String[]{"No", "Correct!", "No", "No"},
            null,
            false,
            null,
            imageUrl,
            false,
            "single"
        );
    }

    public QuizRequest quizRequest(Question... questions) {
        int[] questionIds = Arrays.stream(questions)
            .mapToInt(Question::getId)
            .toArray();

        return new QuizRequest(
            "Test Quiz",
            "Test Description",
            questionIds,
            QuizMode.LEARN,
            Difficulty.KEEP_QUESTION,
            85,
            null,
            null,
            1
        );
    }

    public Question.QuestionBuilder multipleChoiceQuestion() {
        return Question.builder()
            .question("Which are cities in Italy?")
            .answers(new String[]{"Naples", "Rome", "Paris", "Berlin"})
            .correctAnswers(new int[]{0, 1})
            .explanations(new String[]{"Yes!", "Yes!", "No, France", "No, Germany"})
            .isEasyMode(false);
    }

    public Question save(Question.QuestionBuilder builder) {
        return questionRepository.save(builder.build());
    }

    public Question save(Question question) {
        return questionRepository.save(question);
    }

    public Quiz.QuizBuilder quiz(Question... questions) {
        int[] questionIds = Arrays.stream(questions)
            .mapToInt(Question::getId)
            .toArray();

        return Quiz.builder()
            .title("Test Quiz")
            .description("Test Description")
            .mode(QuizMode.LEARN)
            .difficulty(Difficulty.KEEP_QUESTION)
            .passScore(85)
            .questionIds(questionIds)
            .randomQuestionCount(1);
    }

    public Quiz.QuizBuilder quizIn(Workspace workspace) {
        return quiz().workspaceGuid(workspace.getGuid());
    }

    public Quiz save(Quiz.QuizBuilder builder) {
        return quizRepository.save(builder.build());
    }

    public Quiz save(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public Workspace.WorkspaceBuilder workspace() {
        return Workspace.builder()
            .title("Test Workspace");
    }

    public Workspace save(Workspace.WorkspaceBuilder builder) {
        return workspaceRepository.save(builder.build());
    }

    public Workspace save(Workspace workspace) {
        return workspaceRepository.save(workspace);
    }
}
