package cz.scrumdojo.quizmaster.stats;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties("quizId")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class Stats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "started_at", columnDefinition = "dateTime")
    private LocalDateTime startedAt;

    @Column(name = "finished_at", columnDefinition = "dateTime")
    private LocalDateTime finishedAt;

    @Column(name = "score", columnDefinition = "integer")
    private Integer score;

    @Column(name = "quiz_id", columnDefinition = "integer")
    private Integer quizId;
}
