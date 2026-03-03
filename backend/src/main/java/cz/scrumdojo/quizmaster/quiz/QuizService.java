package cz.scrumdojo.quizmaster.quiz;

import cz.scrumdojo.quizmaster.question.Question;
import cz.scrumdojo.quizmaster.question.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        Integer randomCount = quiz.getRandomQuestionCount();
        if (randomCount != null && randomCount > 0 && !questions.isEmpty()) {
            Collections.shuffle(questions);
            questions = new ArrayList<>(questions.subList(0, Math.min(randomCount, questions.size())));
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
            .randomQuestionCount(quiz.getRandomQuestionCount())
            .build();
    }

    private List<Question> loadQuestions(Quiz quiz) {
        List<Integer> ids = Arrays.stream(quiz.getQuestionIds()).boxed().toList();
        Map<Integer, Question> questionsById = questionRepository.findAllById(ids).stream()
            .collect(Collectors.toMap(Question::getId, Function.identity()));
        return ids.stream()
            .map(questionsById::get)
            .filter(Objects::nonNull)
            .toList();
    }
}
