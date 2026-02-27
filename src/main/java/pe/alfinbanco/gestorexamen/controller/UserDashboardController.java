package pe.alfinbanco.gestorexamen.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import pe.alfinbanco.gestorexamen.entity.UserEntity;
import pe.alfinbanco.gestorexamen.service.ExamService;
import pe.alfinbanco.gestorexamen.service.UserService;
import pe.alfinbanco.gestorexamen.util.SecurityUtil;

@Controller
public class UserDashboardController {
    private final UserService userService;
    private final ExamService examService;

    public UserDashboardController(UserService userService, ExamService examService) {
        this.userService = userService;
        this.examService = examService;
    }

    @GetMapping("/user/dashboard")
    public String dashboard(Model model) {
        UserEntity u = userService.getByUsernameOrThrow(SecurityUtil.currentUsername());

        model.addAttribute("username", u.getUsername());
        model.addAttribute("avg", examService.avgByUser(u.getId()));
        model.addAttribute("top10", examService.top10ByUser(u.getId()));
        model.addAttribute("attempts", examService.listAttemptsByUser(u.getId()));

        return "user/dashboard";
    }
}
