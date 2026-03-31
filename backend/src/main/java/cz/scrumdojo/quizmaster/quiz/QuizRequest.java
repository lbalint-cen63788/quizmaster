package cz.scrumdojo.quizmaster.quiz;

import jakarta.validation.constraints.NotBlank;

public record QuizRequest(
    @NotBlank String title,
    String description,
    int[] questionIds,
    QuizMode mode,
    Difficulty difficulty,
    int passScore,
    Integer timeLimit,
    String workspaceGuid,
    Integer randomQuestionCount
) {
    public Quiz toEntity() {
        return Quiz.builder()
            .title(title)
            .description(description)
            .questionIds(questionIds)
            .mode(mode)
            .difficulty(difficulty != null ? difficulty : Difficulty.KEEP_QUESTION)
            .passScore(passScore)
            .timeLimit(timeLimit)
            .workspaceGuid(workspaceGuid)
            .randomQuestionCount(randomQuestionCount)
            .build();
    }
}
