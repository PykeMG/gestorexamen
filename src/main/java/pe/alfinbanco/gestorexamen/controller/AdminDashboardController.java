package pe.alfinbanco.gestorexamen.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import pe.alfinbanco.gestorexamen.entity.ExamAttemptEntity;
import pe.alfinbanco.gestorexamen.repository.ExamAttemptRepository;
import pe.alfinbanco.gestorexamen.service.ExamService;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {
    private static final DateTimeFormatter STARTED_AT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ExamAttemptRepository repo;
    private final ExamService examService;

    public AdminDashboardController(ExamAttemptRepository repo, ExamService examService) {
        this.repo = repo;
        this.examService = examService;
    }

    @GetMapping("/dashboard")
    public String dash(Model model) {
        model.addAttribute("top10global", repo.top10Global());
        model.addAttribute("inProgressAttempts", examService.listInProgressAttempts());
        return "admin/dashboard";
    }

    @GetMapping("/exams/in-progress")
    @ResponseBody
    public List<InProgressAttemptRow> inProgressAttempts() {
        return examService.listInProgressAttempts().stream()
            .map(this::toRow)
            .toList();
    }

    @PostMapping("/exams/{attemptId}/cancel")
    public String cancelAttempt(@PathVariable Long attemptId) {
        examService.cancelAttemptAsAdmin(attemptId);
        return "redirect:/admin/dashboard?canceled";
    }

    private InProgressAttemptRow toRow(ExamAttemptEntity a) {
        return new InProgressAttemptRow(
            a.getId(),
            a.getUser().getUsername(),
            a.getTotalQuestions(),
            a.getTimeLimitMinutes(),
            a.getStartedAt().format(STARTED_AT_FMT)
        );
    }

    public record InProgressAttemptRow(
        Long id,
        String username,
        int totalQuestions,
        int timeLimitMinutes,
        String startedAt
    ) {}
}
