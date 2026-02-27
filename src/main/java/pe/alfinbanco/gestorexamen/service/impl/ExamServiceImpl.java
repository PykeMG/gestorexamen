package pe.alfinbanco.gestorexamen.service.impl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.alfinbanco.gestorexamen.dto.*;
import pe.alfinbanco.gestorexamen.entity.*;
import pe.alfinbanco.gestorexamen.repository.*;
import pe.alfinbanco.gestorexamen.service.ExamService;


@Service
public class ExamServiceImpl implements ExamService{
    private final UserRepository userRepo;
    private final CategoryRepository categoryRepo;
    private final QuestionRepository qRepo;
    private final QuestionOptionRepository oRepo;
    private final ExamAttemptRepository aRepo;
    private final AttemptAnswerRepository ansRepo;

    public ExamServiceImpl(UserRepository userRepo,
                            CategoryRepository categoryRepo,
                            QuestionRepository qRepo,
                            QuestionOptionRepository oRepo,
                            ExamAttemptRepository aRepo,
                            AttemptAnswerRepository ansRepo) {
        this.userRepo = userRepo;
        this.categoryRepo = categoryRepo;
        this.qRepo = qRepo;
        this.oRepo = oRepo;
        this.aRepo = aRepo;
        this.ansRepo = ansRepo;
    }

    @Override
    @Transactional
    public ExamAttemptEntity createAttempt(Long userId, int n, int minutes, List<Long> categoryIds) {
        UserEntity user = userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        List<String> selectedCategories = resolveCategoryNames(categoryIds);
        List<QuestionEntity> qs = selectedCategories.isEmpty()
            ? qRepo.findRandomActive(n)
            : qRepo.findRandomActiveByCategories(n, selectedCategories);

        if (qs.size() < n) {
        throw new IllegalStateException("No hay suficientes preguntas activas");
        }

        ExamAttemptEntity attempt = aRepo.save(new ExamAttemptEntity(user, n, minutes));

        for (QuestionEntity q : qs) {
        Long correctOptionId = oRepo.findByQuestionId(q.getId()).stream()
            .filter(QuestionOptionEntity::isCorrect)
            .map(QuestionOptionEntity::getId)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Pregunta sin respuesta correcta"));

        ansRepo.save(new AttemptAnswerEntity(attempt, q, correctOptionId));
        }

        return attempt;
    }

    @Override
    public long countActiveQuestions(List<Long> categoryIds) {
        List<String> selectedCategories = resolveCategoryNames(categoryIds);
        if (selectedCategories.isEmpty()) {
            return qRepo.countByActiveTrue();
        }
        return qRepo.countActiveByCategories(selectedCategories);
    }

    private List<String> resolveCategoryNames(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }
        return categoryRepo.findAllById(categoryIds).stream()
            .filter(CategoryEntity::isActive)
            .map(CategoryEntity::getName)
            .toList();
    }

    @Override
    public List<QuestionForExamDto> getAttemptQuestions(Long attemptId, Long userId) {
        ExamAttemptEntity attempt = aRepo.findById(attemptId).orElseThrow();
        if (!attempt.getUser().getId().equals(userId)) throw new SecurityException("No autorizado");
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) throw new IllegalStateException("No está en progreso");

        List<AttemptAnswerEntity> rows = ansRepo.findByAttemptIdOrderByIdAsc(attemptId);

        List<QuestionForExamDto> out = new ArrayList<>();
        for (AttemptAnswerEntity r : rows) {
        QuestionEntity q = r.getQuestion();
        List<QuestionForExamDto.OptionDto> opts = oRepo.findByQuestionIdOrderByIdAsc(q.getId()).stream()
            .map(x -> new QuestionForExamDto.OptionDto(x.getId(), x.getOptionText()))
            .toList();

        out.add(new QuestionForExamDto(q.getId(), q.getStatement(), opts));
        }
        return out;
    }

    @Override
    @Transactional
    public void submitAttempt(Long attemptId, Long userId, Map<Long, Long> answersByQuestionId) {
        ExamAttemptEntity attempt = aRepo.findById(attemptId).orElseThrow();
        if (!attempt.getUser().getId().equals(userId)) throw new SecurityException("No autorizado");
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) throw new IllegalStateException("No está en progreso");

        List<AttemptAnswerEntity> rows = ansRepo.findByAttemptIdOrderByIdAsc(attemptId);

        int correctCount = 0;

        for (AttemptAnswerEntity r : rows) {
        Long qId = r.getQuestion().getId();
        Long selected = answersByQuestionId.get(qId);

        r.setSelectedOptionId(selected);

        boolean ok = selected != null && selected.equals(r.getCorrectOptionId());
        r.setCorrect(ok);

        if (ok) correctCount++;

        ansRepo.save(r);
        }

        attempt.setCorrectCount(correctCount);
        attempt.setFinishedAt(LocalDateTime.now());
        attempt.setStatus(AttemptStatus.FINISHED);

        double score = (attempt.getTotalQuestions() == 0) ? 0.0 :
            (20.0 * correctCount) / attempt.getTotalQuestions();

        attempt.setScore(Math.round(score * 100.0) / 100.0);
        aRepo.save(attempt);
    }

    @Override
    public List<ReviewRowDto> getAttemptReview(Long attemptId, Long userId) {
        ExamAttemptEntity attempt = aRepo.findById(attemptId).orElseThrow();
        if (!attempt.getUser().getId().equals(userId)) throw new SecurityException("No autorizado");
        if (attempt.getStatus() != AttemptStatus.FINISHED) throw new IllegalStateException("Aún no finalizado");

        List<AttemptAnswerEntity> rows = ansRepo.findByAttemptIdOrderByIdAsc(attemptId);

        Map<Long, Map<Long, String>> optionTextByQuestion = new HashMap<>();
        for (AttemptAnswerEntity r : rows) {
        Long qId = r.getQuestion().getId();
        optionTextByQuestion.computeIfAbsent(qId, id ->
            oRepo.findByQuestionIdOrderByIdAsc(qId).stream()
                .collect(Collectors.toMap(QuestionOptionEntity::getId, QuestionOptionEntity::getOptionText))
        );
        }

        List<ReviewRowDto> out = new ArrayList<>();
        for (AttemptAnswerEntity r : rows) {
        Long qId = r.getQuestion().getId();
        Map<Long, String> m = optionTextByQuestion.getOrDefault(qId, Map.of());

        Long sel = r.getSelectedOptionId();
        Long cor = r.getCorrectOptionId();

        out.add(new ReviewRowDto(
            qId,
            r.getQuestion().getStatement(),
            sel,
            sel == null ? "(Sin responder)" : m.getOrDefault(sel, "(No encontrado)"),
            cor,
            m.getOrDefault(cor, "(No encontrado)"),
            r.isCorrect()
        ));
        }
        return out;
    }

    @Override
    public List<ExamAttemptEntity> listAttemptsByUser(Long userId) {
        return aRepo.findAllByUserIdOrderByStartedAtDesc(userId);
    }

    @Override
    public List<ExamAttemptEntity> listInProgressAttempts() {
        return aRepo.findInProgressOrderByStartedAtAsc();
    }

    @Override
    @Transactional
    public void cancelAttemptAsAdmin(Long attemptId) {
        ExamAttemptEntity attempt = aRepo.findById(attemptId).orElseThrow();
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) return;

        attempt.setStatus(AttemptStatus.CANCELED);
        attempt.setFinishedAt(LocalDateTime.now());
        attempt.setScore(null);
        attempt.setCorrectCount(null);
        aRepo.save(attempt);
    }

    @Override
    public List<ExamAttemptEntity> top10ByUser(Long userId) {
        return aRepo.findTopFinishedByUser(userId, PageRequest.of(0, 10));
    }

    @Override
    public Double avgByUser(Long userId) {
        Double avg = aRepo.avgScoreByUser(userId);
        if (avg == null) return 0.0;
        return Math.round(avg * 100.0) / 100.0;
    }
}
