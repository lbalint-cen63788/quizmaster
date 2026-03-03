package cz.scrumdojo.quizmaster;
import cz.scrumdojo.quizmaster.question.*;
import java.util.*;

public class QuizUtils {

    public static Question[] shrinkQuestions(Question[] questions, int length) {
        return Arrays.copyOf(questions, length);
    }
}
