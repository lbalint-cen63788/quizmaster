package cz.scrumdojo.quizmaster.quiz;

import cz.scrumdojo.quizmaster.question.Question;
import cz.scrumdojo.quizmaster.question.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    @Autowired
    public QuizService(QuestionRepository questionRepository, QuizRepository quizRepository) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
    }

    public Optional<QuizResponse> getQuiz(Integer id) {
        return quizRepository.findById(id).map(this::toQuizResponse);
    }

    private QuizResponse toQuizResponse(Quiz quiz) {
        List<Question> questions = loadQuestions(quiz);

        if (quiz.getFinalCount() != null && quiz.getFinalCount() > 0 && !questions.isEmpty()) {
            Collections.shuffle(questions);
            questions = new ArrayList<>(questions.subList(0, Math.min(quiz.getFinalCount(), questions.size())));
        }

        return QuizResponse.builder()
            .id(quiz.getId())
            .title(quiz.getTitle())
            .description(quiz.getDescription())
            .questions(questions.toArray(new Question[0]))
            .mode(quiz.getMode())
            .difficulty(quiz.getDifficulty())
            .passScore(quiz.getPassScore())
            .timeLimit(quiz.getTimeLimit())
            .size(quiz.getSize())
            .build();
    }

    private List<Question> loadQuestions(Quiz quiz) {
        int questionsLimit = (quiz.getSize() != null && quiz.getSize() > 0)
            ? quiz.getSize()
            : quiz.getQuestionIds().length;

        List<Question> questions = new ArrayList<>(questionsLimit);
        for (int i = 0; i < questionsLimit; i++) {
            questionRepository.findById(quiz.getQuestionIds()[i]).ifPresent(questions::add);
        }
        return questions;
    }
}
