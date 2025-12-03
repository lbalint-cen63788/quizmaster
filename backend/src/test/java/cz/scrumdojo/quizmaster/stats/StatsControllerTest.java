package cz.scrumdojo.quizmaster.stats;

import cz.scrumdojo.quizmaster.TestFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.time.LocalDateTime;

@SpringBootTest
public class StatsControllerTest {

    @Autowired
    private TestFixtures fixtures;

    @Autowired
    private StatsController statsController;

    @Test
    public void saveAndGetStats() {
        LocalDateTime now = LocalDateTime.now();
        int statId = 1;
        int score = 90;

        Stats stat = fixtures.stats()
                .id(statId)
                .startedAt(now)
                .finishedAt(now.plusMinutes(5))
                .score(score)
                .build();

        statsController.saveStats(statId, stat);

        var returnedStats = statsController.getStats(1);
        var returnedStatsBody = returnedStats.getBody();

        assertNotNull(returnedStats);
        assertEquals(HttpStatus.OK, returnedStats.getStatusCode());
        assertNotNull(returnedStatsBody.getStats());
        assertFalse(returnedStatsBody.getStats().isEmpty());

        assertEquals(1, returnedStatsBody.getStats().get(0).getId());

        assertEquals(now, returnedStatsBody.getStats().get(0).getStartedAt());
        assertEquals(now.plusMinutes(5), returnedStatsBody.getStats().get(0).getFinishedAt());

        assertEquals(score, returnedStatsBody.getStats().get(0).getScore());
    }
}
