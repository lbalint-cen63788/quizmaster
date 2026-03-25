package cz.scrumdojo.quizmaster.aiassistant;

public record AiAssistantResponse(AiAssistantQuestionType type, String question, String[] answers, int[] correctAnswers) {}
