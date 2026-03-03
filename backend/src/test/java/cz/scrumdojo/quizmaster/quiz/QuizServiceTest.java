package cz.scrumdojo.quizmaster.quiz;

import cz.scrumdojo.quizmaster.TestFixtures;
import cz.scrumdojo.quizmaster.question.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class QuizServiceTest {

    @Autowired
    private QuizService quizService;

    @Autowired
    private TestFixtures fixtures;

    @Test
    public void quizWithNoRandomizationReturnsAllQuestions() {
        Question q1 = fixtures.save(fixtures.question());
        Question q2 = fixtures.save(fixtures.question());
        Quiz quiz = fixtures.save(Quiz.builder()
            .title("Test")
            .description("Test")
            .mode(QuizMode.LEARN)
            .difficulty(Difficulty.KEEP_QUESTION)
            .passScore(80)
            .questionIds(new int[]{q1.getId(), q2.getId()})
            .build());

        QuizResponse response = quizService.getQuiz(quiz.getId()).orElseThrow();

        assertEquals(2, response.getQuestions().length);
    }

    @Test
    public void quizWithRandomQuestionCountReturnsExactCount() {
        Question q1 = fixtures.save(fixtures.question());
        Question q2 = fixtures.save(fixtures.question());
        Question q3 = fixtures.save(fixtures.question());
        Quiz quiz = fixtures.save(Quiz.builder()
            .title("Test")
            .description("Test")
            .mode(QuizMode.LEARN)
            .difficulty(Difficulty.KEEP_QUESTION)
            .passScore(80)
            .questionIds(new int[]{q1.getId(), q2.getId(), q3.getId()})
            .finalCount(2)
            .build());

        QuizResponse response = quizService.getQuiz(quiz.getId()).orElseThrow();

        assertEquals(2, response.getQuestions().length);
    }

    @Test
    public void quizWithRandomQuestionCountReturnsSubsetOfPool() {
        Question q1 = fixtures.save(fixtures.question());
        Question q2 = fixtures.save(fixtures.question());
        Question q3 = fixtures.save(fixtures.question());
        Set<Integer> poolIds = Set.of(q1.getId(), q2.getId(), q3.getId());

        Quiz quiz = fixtures.save(Quiz.builder()
            .title("Test")
            .description("Test")
            .mode(QuizMode.LEARN)
            .difficulty(Difficulty.KEEP_QUESTION)
            .passScore(80)
            .questionIds(new int[]{q1.getId(), q2.getId(), q3.getId()})
            .finalCount(2)
            .build());

        QuizResponse response = quizService.getQuiz(quiz.getId()).orElseThrow();

        Set<Integer> returnedIds = Arrays.stream(response.getQuestions())
            .map(Question::getId)
            .collect(Collectors.toSet());
        assertTrue(poolIds.containsAll(returnedIds));
    }

    @Test
    public void nonExistentQuizReturnsEmpty() {
        Optional<QuizResponse> response = quizService.getQuiz(-1);

        assertTrue(response.isEmpty());
    }

    @Test
    public void quizWithMissingQuestionSkipsIt() {
        Question q1 = fixtures.save(fixtures.question());
        Quiz quiz = fixtures.save(Quiz.builder()
            .title("Test")
            .description("Test")
            .mode(QuizMode.LEARN)
            .difficulty(Difficulty.KEEP_QUESTION)
            .passScore(80)
            .questionIds(new int[]{q1.getId(), -999})
            .build());

        QuizResponse response = quizService.getQuiz(quiz.getId()).orElseThrow();

        assertEquals(1, response.getQuestions().length);
        assertEquals(q1.getId(), response.getQuestions()[0].getId());
    }
}
