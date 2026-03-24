package cz.scrumdojo.quizmaster.question;

public record QuestionRequest(
    String question,
    String[] answers,
    int[] correctAnswers,
    String[] explanations,
    String questionExplanation,
    boolean easyMode,
    String workspaceGuid,
    String imageUrl,
    Boolean aiGenerated,
    String questionType,
    Integer tolerance
) {
    public Question toEntity() {
        return Question.builder()
            .question(question)
            .answers(answers)
            .correctAnswers(correctAnswers)
            .explanations(explanations)
            .questionExplanation(questionExplanation)
            .isEasyMode(easyMode)
            .workspaceGuid(workspaceGuid)
            .imageUrl(imageUrl)
            .tolerance(tolerance)
            .build();
    }
}
