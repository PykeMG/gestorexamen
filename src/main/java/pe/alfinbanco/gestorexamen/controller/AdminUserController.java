package pe.alfinbanco.gestorexamen.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pe.alfinbanco.gestorexamen.entity.Role;
import pe.alfinbanco.gestorexamen.entity.UserEntity;
import pe.alfinbanco.gestorexamen.service.UserService;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.listAll());
        return "admin/users";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        UserEntity u = userService.getByIdOrThrow(id);
        model.addAttribute("userData", u);
        model.addAttribute("roles", Role.values());
        return "admin/user-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String username,
                         @RequestParam Role role,
                         @RequestParam(defaultValue = "false") boolean active,
                         @RequestParam(required = false) String newPassword,
                         Model model) {
        try {
            userService.updateByAdmin(id, username, role, active, newPassword);
            return "redirect:/admin/users?ok=updated";
        } catch (IllegalArgumentException ex) {
            UserEntity u = userService.getByIdOrThrow(id);
            u.setUsername(username);
            u.setRole(role);
            u.setActive(active);
            model.addAttribute("userData", u);
            model.addAttribute("roles", Role.values());
            model.addAttribute("error", ex.getMessage());
            return "admin/user-form";
        }
    }
}
