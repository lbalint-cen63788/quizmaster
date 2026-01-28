package cz.scrumdojo.quizmaster.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {

    @Query(value = "SELECT * FROM quiz WHERE workspace_guid = ?", nativeQuery = true)
    java.util.List<Quiz> findByWorkspaceGuid(String guid);

    @Query(value = "SELECT COUNT(*) > 0 FROM quiz WHERE ? = ANY(questions)", nativeQuery = true)
    boolean existsQuizWithQuestionId(int questionId);
}
