package cz.scrumdojo.quizmaster.stats;

import cz.scrumdojo.quizmaster.question.Question;
import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class StatsResponse {

    private List<Stats> stats;
}
