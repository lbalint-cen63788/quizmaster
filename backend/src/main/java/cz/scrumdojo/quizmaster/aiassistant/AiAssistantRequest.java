package cz.scrumdojo.quizmaster.aiassistant;

public record AiAssistantRequest(AiAssistantQuestionType type, String question) {

    public AiAssistantRequest(AiAssistantQuestionType type, String question) {
        this.type = type != null ? type : AiAssistantQuestionType.SINGLE;
        this.question = question;
    }
}
