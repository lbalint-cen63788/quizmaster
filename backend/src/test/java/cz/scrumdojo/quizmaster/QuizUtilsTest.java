package cz.scrumdojo.quizmaster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import cz.scrumdojo.quizmaster.question.*;
import org.junit.jupiter.api.Test;

public class QuizUtilsTest {

    @Test
    public void shrinkQuestions() {
        Question[] questions = new Question[] {
            Question.builder().id(1).build(),
            Question.builder().id(2).build(),
            Question.builder().id(3).build(),
            Question.builder().id(4).build()
        };
        Question[] result = QuizUtils.shrinkQuestions(questions, 3);
        assertEquals(3, result.length);
        assertEquals(1, result[0].getId());
        assertEquals(2, result[1].getId());
        assertEquals(3, result[2].getId());
    }
}
