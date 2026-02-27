package pe.alfinbanco.gestorexamen.service;

import pe.alfinbanco.gestorexamen.dto.QuestionForExamDto;
import pe.alfinbanco.gestorexamen.dto.ReviewRowDto;
import pe.alfinbanco.gestorexamen.entity.ExamAttemptEntity;
import java.util.*;

public interface ExamService {
    ExamAttemptEntity createAttempt(Long userId, int totalQuestions, int timeLimitMinutes, List<Long> categoryIds);
    long countActiveQuestions(List<Long> categoryIds);
    List<QuestionForExamDto> getAttemptQuestions(Long attemptId, Long userId);
    void submitAttempt(Long attemptId, Long userId, Map<Long, Long> answersByQuestionId);
    List<ReviewRowDto> getAttemptReview(Long attemptId, Long userId);
    List<ExamAttemptEntity> listAttemptsByUser(Long userId);
    List<ExamAttemptEntity> listInProgressAttempts();
    void cancelAttemptAsAdmin(Long attemptId);
    List<ExamAttemptEntity> top10ByUser(Long userId);
    Double avgByUser(Long userId);
}
