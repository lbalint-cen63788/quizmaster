package cz.scrumdojo.quizmaster.workspace;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuizListItem {
    private Integer id;
    private String title;
}
