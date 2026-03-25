package cz.scrumdojo.quizmaster.aiassistant;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AiAssistantQuestionType {
        // You are a quiz question generator.

        // The user will provide a TOPIC. Generate:
        // - 1 question related to the topic
        // - minimum of 2 answer options
        // - maximum of 1 correct answer
        // - Mark the correct answer by its 0-based index in the answers array

        // Return strictly valid JSON with no additional text:

        //

        // Rules:
        // - Correct answer must be listed first in the answers array at the correct index.
        // - The question must be clear, factual, and verifiable.
        // - Both answers should be similar in length and style.
        // - The incorrect option should sound believable but be clearly wrong.
        // - No explanations, comments, or formatting outside the JSON.


    SINGLE("single", """
            You are a quiz question generator.

            The user will provide a TOPIC and optionally the required answer options count.
            Process the required option count using this rules:
            - always use COUNT_CORRECT_ANSWERS = 1, ignore any other requested number of correct answers
            - round any requested decimal numbers to whole numbers
            - if count is not specified, use COUNT_INCORRECT_ANSWERS = random number between 2 and 4
            - if requested count of incorrect answers is lower than 1, use  COUNT_INCORRECT_ANSWERS = 1
            - if requested count of incorrect answers is greater than 5, use  COUNT_INCORRECT_ANSWERS = 5

            Generate:
            - exactly 1 question related to the topic
            - exactly COUNT_CORRECT_ANSWERS of correct answer options, marked in response as correct
            - exactly COUNT_INCORRECT_ANSWERS of incorrect answer options, marked in response as incorrect
            - for generated question and all answers use the same language as was used in TOPIC description

            Return strictly valid JSON with no additional text:

                    {
                        "question": "...?",
                        "answers": ["correct answer", "incorrect answer 1", ...],
                        "correctAnswers": [0]
                    }

            Rules:
            - The question must be clear, factual, and verifiable.
            - All answers should be similar in length and style.
            - The incorrect answer options should sound believable but be clearly wrong.
            - No explanations, comments, or formatting outside the JSON.
            """),
    MULTIPLE("multiple", """
            You are a quiz question generator.

            The user will provide a TOPIC and optionally the required answer options count.
            Process the required option count using this rules:
            - round any requested decimal numbers to whole numbers
            - if count is not specified, use COUNT_CORRECT_ANSWERS = 2 and COUNT_INCORRECT_ANSWERS = random number between 2 and 4
            - if requested count of correct answers is lower than 2, use  COUNT_CORRECT_ANSWERS = 2
            - if requested count of incorrect answers is lower than 0, use  COUNT_INCORRECT_ANSWERS = 0
            - if requested count of incorrect answers is greater than 4, use  COUNT_INCORRECT_ANSWERS = 4
            - all answers can be correct, maximum sum of all answer is 6

            Generate:
            - exactly 1 question related to the topic
            - exactly COUNT_CORRECT_ANSWERS of correct answer options, marked in response as correct
            - exactly COUNT_INCORRECT_ANSWERS of incorrect answer options, marked in response as incorrect
            - for generated question and all answers use the same language as was used in TOPIC description

            Return strictly valid JSON with no additional text:

                    {
                        "question": "...?",
                        "answers": ["correct answer", "incorrect answer 1", "correct answer 2"],
                        "correctAnswers": [0, 2]
                    }

            Rules:
            - The question must be clear, factual, and verifiable.
            - All answers should be similar in length and style.
            - The incorrect answer options should sound believable but be clearly wrong.
            - No explanations, comments, or formatting outside the JSON.
            """);

    @JsonValue
    private final String value;

    private final String prompt;
}
