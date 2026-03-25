package cz.scrumdojo.quizmaster.attempt;

import cz.scrumdojo.quizmaster.TestFixtures;
import cz.scrumdojo.quizmaster.question.Question;
import cz.scrumdojo.quizmaster.quiz.Quiz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AttemptControllerTest {

    @Autowired
    private AttemptController attemptController;

    @Autowired
    private TestFixtures fixtures;

    @Test
    public void createAndGetAttempt() {
        // Given: A quiz exists
        Question question = fixtures.save(fixtures.question());
        Quiz quiz = fixtures.save(fixtures.quiz(question));

        // When: Creating a new attempt
        AttemptRequest request = fixtures.attemptRequest(quiz);
        ResponseEntity<AttemptResponse> createResponse = attemptController.createAttempt(request);

        // Then: Attempt is created successfully
        assertNotNull(createResponse.getBody(), "Create response should not be null");
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());

        AttemptResponse created = createResponse.getBody();
        assertNotNull(created.id(), "Attempt ID should not be null after creation");
        assertEquals(quiz.getId(), created.quizId());
        assertEquals(request.durationSeconds(), created.durationSeconds());
        assertEquals(request.points(), created.points());
        assertEquals(request.score(), created.score());
        assertEquals(request.status(), created.status());
        assertEquals(request.maxScore(), created.maxScore());

        // And: Getting the attempt by ID returns the same data
        ResponseEntity<AttemptResponse> getResponse = attemptController.getAttempt(created.id());
        assertNotNull(getResponse.getBody());
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        AttemptResponse retrieved = getResponse.getBody();
        assertEquals(created.id(), retrieved.id());
        assertEquals(created.quizId(), retrieved.quizId());
        assertEquals(created.status(), retrieved.status());
    }

    @Test
    public void getAttemptsByQuiz() {
        // Given: A quiz with multiple attempts
        Question question = fixtures.save(fixtures.question());
        Quiz quiz = fixtures.save(fixtures.quiz(question));

        Attempt attempt1 = fixtures.save(fixtures.attempt(quiz));
        Attempt attempt2 = fixtures.save(fixtures.attemptTimedOut(quiz));
        Attempt attempt3 = fixtures.save(fixtures.attemptInProgress(quiz));

        // When: Getting all attempts for the quiz
        ResponseEntity<List<AttemptResponse>> response = attemptController.getAttemptsByQuiz(quiz.getId());

        // Then: All attempts are returned
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<AttemptResponse> attempts = response.getBody();
        assertTrue(attempts.size() >= 3, "Should have at least 3 attempts");

        // Verify the attempts contain our created ones
        assertTrue(attempts.stream().anyMatch(a -> a.id().equals(attempt1.getId())));
        assertTrue(attempts.stream().anyMatch(a -> a.id().equals(attempt2.getId())));
        assertTrue(attempts.stream().anyMatch(a -> a.id().equals(attempt3.getId())));
    }

    @Test
    public void getAttemptsByQuiz_OrderedByStartedAtDesc() {
        // Given: A quiz with attempts created at different times
        Question question = fixtures.save(fixtures.question());
        Quiz quiz = fixtures.save(fixtures.quiz(question));

        Attempt older = fixtures.save(
            fixtures.attempt(quiz).startedAt(LocalDateTime.now().minusHours(2))
        );
        Attempt newer = fixtures.save(
            fixtures.attempt(quiz).startedAt(LocalDateTime.now().minusHours(1))
        );

        // When: Getting all attempts
        ResponseEntity<List<AttemptResponse>> response = attemptController.getAttemptsByQuiz(quiz.getId());

        // Then: Attempts are ordered by startedAt descending (newest first)
        assertNotNull(response.getBody());
        List<AttemptResponse> attempts = response.getBody();

        int newerIndex = -1;
        int olderIndex = -1;
        for (int i = 0; i < attempts.size(); i++) {
            if (attempts.get(i).id().equals(newer.getId())) newerIndex = i;
            if (attempts.get(i).id().equals(older.getId())) olderIndex = i;
        }

        assertTrue(newerIndex >= 0 && olderIndex >= 0, "Both attempts should be in the list");
        assertTrue(newerIndex < olderIndex, "Newer attempt should come before older attempt");
    }

    @Test
    public void updateAttempt() {
        // Given: An existing attempt
        Question question = fixtures.save(fixtures.question());
        Quiz quiz = fixtures.save(fixtures.quiz(question));
        Attempt attempt = fixtures.save(fixtures.attemptInProgress(quiz));

        // When: Updating the attempt to FINISHED status
        LocalDateTime finishedAt = LocalDateTime.now();
        AttemptRequest updateRequest = new AttemptRequest(
            quiz.getId(),
            180,
            new BigDecimal("3.0"),
            new BigDecimal("100.00"),
            AttemptStatus.FINISHED,
            3,
            attempt.getStartedAt(),
            finishedAt
        );

        ResponseEntity<AttemptResponse> response = attemptController.updateAttempt(attempt.getId(), updateRequest);

        // Then: Attempt is updated successfully
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        AttemptResponse updated = response.getBody();
        assertEquals(attempt.getId(), updated.id());
        assertEquals(180, updated.durationSeconds());
        assertEquals(new BigDecimal("3.0"), updated.points());
        assertEquals(new BigDecimal("100.00"), updated.score());
        assertEquals(AttemptStatus.FINISHED, updated.status());
        assertNotNull(updated.finishedAt());
    }

    @Test
    public void deleteAttempt() {
        // Given: An existing attempt
        Question question = fixtures.save(fixtures.question());
        Quiz quiz = fixtures.save(fixtures.quiz(question));
        Attempt attempt = fixtures.save(fixtures.attempt(quiz));
        Integer attemptId = attempt.getId();

        // When: Deleting the attempt
        ResponseEntity<Void> deleteResponse = attemptController.deleteAttempt(attemptId);

        // Then: Attempt is deleted successfully
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // And: Getting the deleted attempt returns 404
        ResponseEntity<AttemptResponse> getResponse = attemptController.getAttempt(attemptId);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    public void deleteAttempt_NotFound() {
        // Given: A non-existent attempt ID
        Integer nonExistentId = 999999;

        // When: Attempting to delete it
        ResponseEntity<Void> response = attemptController.deleteAttempt(nonExistentId);

        // Then: Returns 404
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getAttempt_NotFound() {
        // Given: A non-existent attempt ID
        Integer nonExistentId = 999999;

        // When: Attempting to get it
        ResponseEntity<AttemptResponse> response = attemptController.getAttempt(nonExistentId);

        // Then: Returns 404
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void createAttempt_WithDifferentStatuses() {
        // Given: A quiz
        Question question = fixtures.save(fixtures.question());
        Quiz quiz = fixtures.save(fixtures.quiz(question));

        // When: Creating attempts with different statuses
        AttemptRequest finishedRequest = new AttemptRequest(
            quiz.getId(), 100, new BigDecimal("2.0"), new BigDecimal("100"),
            AttemptStatus.FINISHED, 2, LocalDateTime.now().minusMinutes(2), LocalDateTime.now()
        );
        AttemptRequest timeoutRequest = new AttemptRequest(
            quiz.getId(), 300, new BigDecimal("1.0"), new BigDecimal("50"),
            AttemptStatus.TIMEOUT, 2, LocalDateTime.now().minusMinutes(5), LocalDateTime.now()
        );
        AttemptRequest inProgressRequest = new AttemptRequest(
            quiz.getId(), 0, BigDecimal.ZERO, BigDecimal.ZERO,
            AttemptStatus.IN_PROGRESS, 0, LocalDateTime.now(), null
        );

        // Then: All attempts are created with correct statuses
        AttemptResponse finished = attemptController.createAttempt(finishedRequest).getBody();
        assertNotNull(finished);
        assertEquals(AttemptStatus.FINISHED, finished.status());

        AttemptResponse timeout = attemptController.createAttempt(timeoutRequest).getBody();
        assertNotNull(timeout);
        assertEquals(AttemptStatus.TIMEOUT, timeout.status());

        AttemptResponse inProgress = attemptController.createAttempt(inProgressRequest).getBody();
        assertNotNull(inProgress);
        assertEquals(AttemptStatus.IN_PROGRESS, inProgress.status());
    }

    @Test
    public void getAttemptsByQuiz_EmptyList() {
        // Given: A quiz with no attempts
        Question question = fixtures.save(fixtures.question());
        Quiz quiz = fixtures.save(fixtures.quiz(question));

        // When: Getting attempts for the quiz
        ResponseEntity<List<AttemptResponse>> response = attemptController.getAttemptsByQuiz(quiz.getId());

        // Then: Returns empty list
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    public void createAttempt_WithDecimalPoints() {
        // Given: A quiz
        Question question = fixtures.save(fixtures.question());
        Quiz quiz = fixtures.save(fixtures.quiz(question));

        // When: Creating an attempt with decimal points
        AttemptRequest request = new AttemptRequest(
            quiz.getId(), 90, new BigDecimal("2.5"), new BigDecimal("83.33"),
            AttemptStatus.FINISHED, 3, LocalDateTime.now().minusMinutes(2), LocalDateTime.now()
        );

        ResponseEntity<AttemptResponse> response = attemptController.createAttempt(request);

        // Then: Decimal values are preserved
        assertNotNull(response.getBody());
        AttemptResponse created = response.getBody();
        assertEquals(new BigDecimal("2.5"), created.points());
        assertEquals(new BigDecimal("83.33"), created.score());
    }
}

