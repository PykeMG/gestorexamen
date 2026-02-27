package pe.alfinbanco.gestorexamen.controller;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pe.alfinbanco.gestorexamen.entity.CategoryEntity;
import pe.alfinbanco.gestorexamen.repository.CategoryRepository;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    private final CategoryRepository categoryRepository;

    public AdminCategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name")));
        return "admin/categories";
    }

    @GetMapping("/new")
    public String createForm() {
        return "admin/category-form";
    }

    @PostMapping
    public String create(@RequestParam String name,
                         @RequestParam(defaultValue = "false") boolean active,
                         Model model) {
        String cleaned = clean(name);
        if (cleaned == null) {
            model.addAttribute("error", "El nombre es obligatorio.");
            model.addAttribute("name", name);
            model.addAttribute("active", active);
            return "admin/category-form";
        }
        if (categoryRepository.existsByName(cleaned)) {
            model.addAttribute("error", "La categoría ya existe.");
            model.addAttribute("name", cleaned);
            model.addAttribute("active", active);
            return "admin/category-form";
        }
        categoryRepository.save(new CategoryEntity(cleaned, active));
        return "redirect:/admin/categories?ok=created";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryRepository.findById(id).orElseThrow());
        return "admin/category-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam(defaultValue = "false") boolean active,
                         Model model) {
        CategoryEntity existing = categoryRepository.findById(id).orElseThrow();
        String cleaned = clean(name);
        if (cleaned == null) {
            model.addAttribute("error", "El nombre es obligatorio.");
            existing.setName(name);
            existing.setActive(active);
            model.addAttribute("category", existing);
            return "admin/category-form";
        }
        if (categoryRepository.existsByNameAndIdNot(cleaned, id)) {
            model.addAttribute("error", "La categoría ya existe.");
            existing.setName(cleaned);
            existing.setActive(active);
            model.addAttribute("category", existing);
            return "admin/category-form";
        }
        existing.setName(cleaned);
        existing.setActive(active);
        categoryRepository.save(existing);
        return "redirect:/admin/categories?ok=updated";
    }

    private String clean(String s) {
        if (s == null) return null;
        String trimmed = s.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
