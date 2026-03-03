package cz.scrumdojo.quizmaster.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {

    List<Quiz> findByWorkspaceGuid(String workspaceGuid);

    @Query(value = "SELECT COUNT(*) > 0 FROM quiz WHERE ? = ANY(questions)", nativeQuery = true)
    boolean existsQuizWithQuestionId(int questionId);
}
