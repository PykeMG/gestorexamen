package pe.alfinbanco.gestorexamen.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import pe.alfinbanco.gestorexamen.entity.AttemptStatus;
import pe.alfinbanco.gestorexamen.entity.ExamAttemptEntity;
import pe.alfinbanco.gestorexamen.entity.UserEntity;
import pe.alfinbanco.gestorexamen.repository.CategoryRepository;
import pe.alfinbanco.gestorexamen.service.ExamService;
import pe.alfinbanco.gestorexamen.service.UserService;
import pe.alfinbanco.gestorexamen.util.SecurityUtil;

@Controller
@Validated
@RequestMapping("/user")
public class ExamController {
    private static final Logger log = LoggerFactory.getLogger(ExamController.class);
    private final UserService userService;
    private final ExamService examService;
    private final CategoryRepository categoryRepository;

    public ExamController(UserService userService, ExamService examService, CategoryRepository categoryRepository) {
        this.userService = userService;
        this.examService = examService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/exam/new")
    public String newExam(Model model) {
        var categories = categoryRepository.findByActiveTrueOrderByNameAsc();
        List<Long> selectedCategoryIds = categories.stream().map(c -> c.getId()).toList();
        long maxQuestions = examService.countActiveQuestions(selectedCategoryIds);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryIds", selectedCategoryIds);
        model.addAttribute("maxQuestions", maxQuestions);
        return "user/exam-config";
    }

    @PostMapping("/exam/start")
    public String start(@RequestParam @Min(1) @Max(50) int totalQuestions,
                        @RequestParam @Min(1) @Max(180) int timeLimitMinutes,
                        @RequestParam(required = false) List<Long> categoryIds) {

        if (categoryIds == null || categoryIds.isEmpty()) {
            return "redirect:/user/exam/new?error=categories";
        }

        long maxQuestions = 0;
        try {
            maxQuestions = examService.countActiveQuestions(categoryIds);
            if (totalQuestions > maxQuestions) {
                return "redirect:/user/exam/new?error=insufficient&available=" + maxQuestions;
            }

            UserEntity u = userService.getByUsernameOrThrow(SecurityUtil.currentUsername());
            ExamAttemptEntity attempt = examService.createAttempt(u.getId(), totalQuestions, timeLimitMinutes, categoryIds);
            return "redirect:/user/exam/" + attempt.getId();
        } catch (IllegalStateException ex) {
            return "redirect:/user/exam/new?error=insufficient&available=" + maxQuestions;
        } catch (Exception ex) {
            log.error("Error generando examen. totalQuestions={}, timeLimitMinutes={}", totalQuestions, timeLimitMinutes, ex);
            return "redirect:/user/exam/new?error=unexpected";
        }
    }

    @GetMapping("/exam/{attemptId}")
    public String take(@PathVariable Long attemptId, Model model) {
        UserEntity u = userService.getByUsernameOrThrow(SecurityUtil.currentUsername());
        Optional<ExamAttemptEntity> attemptOpt = findAttemptForUser(attemptId, u.getId());
        if (attemptOpt.isEmpty()) {
            return "redirect:/user/results";
        }

        ExamAttemptEntity attempt = attemptOpt.get();
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            return redirectByAttemptStatus(attempt);
        }

        try {
            model.addAttribute("attemptId", attemptId);
            model.addAttribute("questions", examService.getAttemptQuestions(attemptId, u.getId()));
        } catch (IllegalStateException ex) {
            Optional<ExamAttemptEntity> refreshedAttempt = findAttemptForUser(attemptId, u.getId());
            return refreshedAttempt.map(this::redirectByAttemptStatus).orElse("redirect:/user/results");
        }

        model.addAttribute("timeLimitMinutes", attempt.getTimeLimitMinutes());
        return "user/exam-taking";
    }

    @PostMapping("/exam/{attemptId}/submit")
    public String submit(@PathVariable Long attemptId, @RequestParam Map<String, String> params) {
        UserEntity u = userService.getByUsernameOrThrow(SecurityUtil.currentUsername());

        Map<Long, Long> answers = new HashMap<>();
        for (var e : params.entrySet()) {
        if (e.getKey().startsWith("q_")) {
            Long qId = Long.parseLong(e.getKey().substring(2));
            Long optId = Long.parseLong(e.getValue());
            answers.put(qId, optId);
        }
        }

        try {
            examService.submitAttempt(attemptId, u.getId(), answers);
        } catch (IllegalStateException ex) {
            Optional<ExamAttemptEntity> attempt = findAttemptForUser(attemptId, u.getId());
            return attempt.map(this::redirectByAttemptStatus).orElse("redirect:/user/results");
        }
        return "redirect:/user/results/" + attemptId;
    }

    @GetMapping("/exam/{attemptId}/status")
    @ResponseBody
    public Map<String, Object> examStatus(@PathVariable Long attemptId) {
        UserEntity u = userService.getByUsernameOrThrow(SecurityUtil.currentUsername());
        Optional<ExamAttemptEntity> attempt = findAttemptForUser(attemptId, u.getId());

        if (attempt.isEmpty()) {
            return Map.of(
                "status", "NOT_FOUND",
                "closed", true,
                "redirectUrl", "/user/results"
            );
        }

        ExamAttemptEntity a = attempt.get();
        boolean closed = a.getStatus() != AttemptStatus.IN_PROGRESS;
        return Map.of(
            "status", a.getStatus().name(),
            "closed", closed,
            "redirectUrl", redirectUrlByAttemptStatus(a)
        );
    }

    @GetMapping("/results")
    public String results(Model model) {
        UserEntity u = userService.getByUsernameOrThrow(SecurityUtil.currentUsername());
        model.addAttribute("attempts", examService.listAttemptsByUser(u.getId()));
        return "user/results";
    }

    @GetMapping("/results/{attemptId}")
    public String review(@PathVariable Long attemptId, Model model) {
        UserEntity u = userService.getByUsernameOrThrow(SecurityUtil.currentUsername());
        model.addAttribute("attemptId", attemptId);
        model.addAttribute("reviewRows", examService.getAttemptReview(attemptId, u.getId()));
        return "user/result-detail";
    }

    private Optional<ExamAttemptEntity> findAttemptForUser(Long attemptId, Long userId) {
        return examService.listAttemptsByUser(userId).stream()
            .filter(x -> x.getId().equals(attemptId))
            .findFirst();
    }

    private String redirectByAttemptStatus(ExamAttemptEntity attempt) {
        return "redirect:" + redirectUrlByAttemptStatus(attempt);
    }

    private String redirectUrlByAttemptStatus(ExamAttemptEntity attempt) {
        if (attempt.getStatus() == AttemptStatus.CANCELED) {
            return "/user/results?canceled=" + attempt.getId();
        }
        if (attempt.getStatus() == AttemptStatus.FINISHED) {
            return "/user/results/" + attempt.getId();
        }
        return "/user/exam/" + attempt.getId();
    }
}
