package cz.scrumdojo.quizmaster.workspace;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionListItem {
    private Integer id;
    private String question;
    private String editId;
    private Boolean isInAnyQuiz;
}
