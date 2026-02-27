package pe.alfinbanco.gestorexamen.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import pe.alfinbanco.gestorexamen.repository.ExamAttemptRepository;
import pe.alfinbanco.gestorexamen.service.ExamService;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {
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

    @PostMapping("/exams/{attemptId}/cancel")
    public String cancelAttempt(@PathVariable Long attemptId) {
        examService.cancelAttemptAsAdmin(attemptId);
        return "redirect:/admin/dashboard?canceled";
    }
}
