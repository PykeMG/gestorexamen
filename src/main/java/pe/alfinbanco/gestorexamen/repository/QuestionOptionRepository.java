package pe.alfinbanco.gestorexamen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import pe.alfinbanco.gestorexamen.entity.QuestionOptionEntity;

public interface QuestionOptionRepository extends JpaRepository<QuestionOptionEntity, Long> {
    List<QuestionOptionEntity> findByQuestionId(Long questionId);
    List<QuestionOptionEntity> findByQuestionIdOrderByIdAsc(Long questionId);
}
