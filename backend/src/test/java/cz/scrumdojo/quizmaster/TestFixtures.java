package cz.scrumdojo.quizmaster;

import cz.scrumdojo.quizmaster.attempt.Attempt;
import cz.scrumdojo.quizmaster.attempt.AttemptRepository;
import cz.scrumdojo.quizmaster.attempt.AttemptRequest;
import cz.scrumdojo.quizmaster.attempt.AttemptStatus;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class TestFixtures {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private AttemptRepository attemptRepository;

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
            "single",
            null
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
            "multiple",
            null
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
            "single",
            null
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

    public Attempt.AttemptBuilder attempt(Quiz quiz) {
        return Attempt.builder()
            .quizId(quiz.getId())
            .durationSeconds(120)
            .points(new BigDecimal("2.5"))
            .score(new BigDecimal("83.33"))
            .status(AttemptStatus.FINISHED)
            .maxScore(3)
            .startedAt(LocalDateTime.now().minusMinutes(2))
            .finishedAt(LocalDateTime.now());
    }

    public Attempt.AttemptBuilder attemptInProgress(Quiz quiz) {
        return Attempt.builder()
            .quizId(quiz.getId())
            .durationSeconds(0)
            .points(BigDecimal.ZERO)
            .score(BigDecimal.ZERO)
            .status(AttemptStatus.IN_PROGRESS)
            .maxScore(0)
            .startedAt(LocalDateTime.now())
            .finishedAt(null);
    }

    public Attempt.AttemptBuilder attemptTimedOut(Quiz quiz) {
        return Attempt.builder()
            .quizId(quiz.getId())
            .durationSeconds(300)
            .points(new BigDecimal("1.0"))
            .score(new BigDecimal("33.33"))
            .status(AttemptStatus.TIMEOUT)
            .maxScore(3)
            .startedAt(LocalDateTime.now().minusMinutes(5))
            .finishedAt(LocalDateTime.now());
    }

    public AttemptRequest attemptRequest(Quiz quiz) {
        return new AttemptRequest(
            quiz.getId(),
            120,
            new BigDecimal("2.5"),
            new BigDecimal("83.33"),
            AttemptStatus.FINISHED,
            3,
            LocalDateTime.now().minusMinutes(2),
            LocalDateTime.now()
        );
    }

    public Attempt save(Attempt.AttemptBuilder builder) {
        return attemptRepository.save(builder.build());
    }

    public Attempt save(Attempt attempt) {
        return attemptRepository.save(attempt);
    }
}
