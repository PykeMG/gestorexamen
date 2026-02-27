package pe.alfinbanco.gestorexamen.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.alfinbanco.gestorexamen.entity.AttemptAnswerEntity;

public interface AttemptAnswerRepository extends JpaRepository<AttemptAnswerEntity, Long> {
    List<AttemptAnswerEntity> findByAttemptIdOrderByIdAsc(Long attemptId);
}
