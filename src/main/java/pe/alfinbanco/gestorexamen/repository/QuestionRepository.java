package pe.alfinbanco.gestorexamen.repository;

import org.springframework.data.jpa.repository.*;
import java.util.List;
import org.springframework.data.repository.query.Param;
import pe.alfinbanco.gestorexamen.entity.Difficulty;
import pe.alfinbanco.gestorexamen.entity.QuestionEntity;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    // NOTE: Using positional parameter (?1) avoids issues like "no viable alternative" with LIMIT :param in some setups
    @Query(value = "SELECT * FROM questions q WHERE q.active = 1 ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<QuestionEntity> findRandomActive(int n);

    @Query(value = "SELECT * FROM questions q WHERE q.active = 1 AND q.category IN (:categories) ORDER BY RAND() LIMIT :n", nativeQuery = true)
    List<QuestionEntity> findRandomActiveByCategories(int n, List<String> categories);

    long countByActiveTrue();

    @Query(value = "SELECT COUNT(*) FROM questions q WHERE q.active = 1 AND q.category IN (:categories)", nativeQuery = true)
    long countActiveByCategories(List<String> categories);

    List<QuestionEntity> findByActiveTrueOrderByUpdatedAtDesc();

    @Query("""
        SELECT q
        FROM QuestionEntity q
        WHERE (:category IS NULL OR q.category = :category)
          AND (:difficulty IS NULL OR q.difficulty = :difficulty)
          AND (:active IS NULL OR q.active = :active)
        ORDER BY q.updatedAt DESC
    """)
    List<QuestionEntity> findForAdminFilters(@Param("category") String category,
                                             @Param("difficulty") Difficulty difficulty,
                                             @Param("active") Boolean active);
}
