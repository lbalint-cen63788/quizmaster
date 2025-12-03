package cz.scrumdojo.quizmaster.stats;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class StatsController {

    private final StatsRepository statsRepository;

    @Autowired
    public StatsController(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Transactional
    @GetMapping("/quiz/{quizId}/stats")
    public ResponseEntity<StatsResponse> getStats(@PathVariable Integer quizId) {
        StatsResponse response = new StatsResponse();
        response.setStats(new ArrayList<>());
        Stats stats = statsRepository.findByQuizId(quizId).stream().findFirst().orElse(null);
        response.getStats().add(stats);
        return ResponseEntity.ok(response);
    }

    @Transactional
    @PostMapping("/quiz/{quizId}/stats")
    public Stats saveStats(@PathVariable Integer quizId, @RequestBody Stats stats) {
        stats.setQuizId(quizId);
        var createdStats = statsRepository.save(stats);
        return createdStats;
    }
}
