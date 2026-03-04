package cz.scrumdojo.quizmaster.aiassistant;

public record AiAssistantResponse(
	String question,
	String correctAnswerText,
	String incorrectAnswerText,
	String correctAnswer
) {}
