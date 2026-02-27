package pe.alfinbanco.gestorexamen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.alfinbanco.gestorexamen.entity.ExamAttemptEntity;
import java.util.List;

public interface ExamAttemptRepository extends JpaRepository<ExamAttemptEntity, Long> {
    @Query("select a from ExamAttemptEntity a where a.user.id=:userId order by a.startedAt desc")
    List<ExamAttemptEntity> findAllByUserIdOrderByStartedAtDesc(@Param("userId") Long userId);

    @Query("select a from ExamAttemptEntity a where a.user.id=:userId and a.status='FINISHED' and a.score is not null order by a.score desc")
    List<ExamAttemptEntity> findTopFinishedByUser(@Param("userId") Long userId,
                                                    org.springframework.data.domain.Pageable pageable);

    @Query("select avg(a.score) from ExamAttemptEntity a where a.user.id=:userId and a.status='FINISHED' and a.score is not null")
    Double avgScoreByUser(@Param("userId") Long userId);

    @Query("select a from ExamAttemptEntity a join fetch a.user where a.status='IN_PROGRESS' order by a.startedAt asc")
    List<ExamAttemptEntity> findInProgressOrderByStartedAtAsc();

    @Query(value = """
        SELECT u.username, ea.score, ea.finished_at
        FROM exam_attempts ea
        JOIN users u ON u.id = ea.user_id
        WHERE ea.status='FINISHED' AND ea.score IS NOT NULL
        ORDER BY ea.score DESC, ea.finished_at ASC
        LIMIT 10
        """, nativeQuery = true)
    List<Object[]> top10Global();
}
