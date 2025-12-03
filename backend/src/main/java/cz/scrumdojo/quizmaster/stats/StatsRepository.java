package cz.scrumdojo.quizmaster.stats;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StatsRepository extends JpaRepository<Stats, Integer> {

    @Query(value = "SELECT * FROM stats WHERE quiz_id = ?", nativeQuery = true)
    List<Stats> findByQuizId(int quizId);
}
