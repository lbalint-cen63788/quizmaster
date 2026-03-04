package cz.scrumdojo.quizmaster.workspace;

public record QuestionListItem(Integer id, String question, String editId, Boolean isInAnyQuiz, String imageUrl) {}
