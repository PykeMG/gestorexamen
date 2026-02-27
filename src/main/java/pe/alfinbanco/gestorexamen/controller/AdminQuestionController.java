package pe.alfinbanco.gestorexamen.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pe.alfinbanco.gestorexamen.entity.CategoryEntity;
import pe.alfinbanco.gestorexamen.entity.Difficulty;
import pe.alfinbanco.gestorexamen.entity.QuestionEntity;
import pe.alfinbanco.gestorexamen.entity.QuestionOptionEntity;
import pe.alfinbanco.gestorexamen.repository.CategoryRepository;
import pe.alfinbanco.gestorexamen.repository.QuestionOptionRepository;
import pe.alfinbanco.gestorexamen.repository.QuestionRepository;

@Controller
@RequestMapping("/admin/questions")
public class AdminQuestionController {
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository optionRepository;

    public AdminQuestionController(CategoryRepository categoryRepository,
                                   QuestionRepository questionRepository,
                                   QuestionOptionRepository optionRepository) {
        this.categoryRepository = categoryRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("questions", questionRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt")));
        return "admin/questions";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        List<CategoryEntity> categories = categoryRepository.findByActiveTrueOrderByNameAsc();
        model.addAttribute("isEdit", false);
        model.addAttribute("difficulties", Difficulty.values());
        model.addAttribute("categories", categories);
        model.addAttribute("form", QuestionForm.empty());
        return "admin/question-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        QuestionEntity question = questionRepository.findById(id).orElseThrow();
        List<QuestionOptionEntity> options = optionRepository.findByQuestionIdOrderByIdAsc(id);
        List<CategoryEntity> categories = categoryRepository.findByActiveTrueOrderByNameAsc();
        Long selectedCategoryId = categoryRepository.findByName(question.getCategory()).map(CategoryEntity::getId).orElse(null);

        QuestionForm form = QuestionForm.from(question, options, selectedCategoryId);
        model.addAttribute("isEdit", true);
        model.addAttribute("questionId", id);
        model.addAttribute("difficulties", Difficulty.values());
        model.addAttribute("categories", categories);
        model.addAttribute("form", form);
        return "admin/question-form";
    }

    @PostMapping
    @Transactional
    public String create(
        @RequestParam String statement,
        @RequestParam Long categoryId,
        @RequestParam Difficulty difficulty,
        @RequestParam(defaultValue = "false") boolean active,
        @RequestParam String option1,
        @RequestParam String option2,
        @RequestParam String option3,
        @RequestParam String option4,
        @RequestParam int correctIndex,
        Model model
    ) {
        QuestionForm form = new QuestionForm(statement, categoryId, difficulty, active, option1, option2, option3, option4, correctIndex);
        List<String> errors = validateForm(form);
        if (!errors.isEmpty()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("difficulties", Difficulty.values());
            model.addAttribute("categories", categoryRepository.findByActiveTrueOrderByNameAsc());
            model.addAttribute("form", form);
            model.addAttribute("errors", errors);
            return "admin/question-form";
        }

        String categoryName = resolveCategoryName(categoryId);
        QuestionEntity question = new QuestionEntity();
        question.setStatement(clean(statement));
        question.setCategory(categoryName);
        question.setDifficulty(difficulty);
        question.setActive(active);
        QuestionEntity saved = questionRepository.save(question);

        upsertOptions(saved, form, List.of());
        return "redirect:/admin/questions?ok=created";
    }

    @PostMapping("/{id}")
    @Transactional
    public String update(
        @PathVariable Long id,
        @RequestParam String statement,
        @RequestParam Long categoryId,
        @RequestParam Difficulty difficulty,
        @RequestParam(defaultValue = "false") boolean active,
        @RequestParam String option1,
        @RequestParam String option2,
        @RequestParam String option3,
        @RequestParam String option4,
        @RequestParam int correctIndex,
        Model model
    ) {
        QuestionEntity question = questionRepository.findById(id).orElseThrow();
        List<QuestionOptionEntity> existing = optionRepository.findByQuestionIdOrderByIdAsc(id);

        QuestionForm form = new QuestionForm(statement, categoryId, difficulty, active, option1, option2, option3, option4, correctIndex);
        List<String> errors = validateForm(form);
        if (!errors.isEmpty()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("questionId", id);
            model.addAttribute("difficulties", Difficulty.values());
            model.addAttribute("categories", categoryRepository.findByActiveTrueOrderByNameAsc());
            model.addAttribute("form", form);
            model.addAttribute("errors", errors);
            return "admin/question-form";
        }

        String categoryName = resolveCategoryName(categoryId);
        question.setStatement(clean(statement));
        question.setCategory(categoryName);
        question.setDifficulty(difficulty);
        question.setActive(active);
        QuestionEntity saved = questionRepository.save(question);

        upsertOptions(saved, form, existing);
        return "redirect:/admin/questions?ok=updated";
    }

    private void upsertOptions(QuestionEntity question, QuestionForm form, List<QuestionOptionEntity> existing) {
        String[] optionsText = {clean(form.option1), clean(form.option2), clean(form.option3), clean(form.option4)};
        int correctIndex = form.correctIndex;

        for (int i = 0; i < optionsText.length; i++) {
            QuestionOptionEntity opt;
            if (i < existing.size()) {
                opt = existing.get(i);
            } else {
                opt = new QuestionOptionEntity();
                opt.setQuestion(question);
            }
            opt.setOptionText(optionsText[i]);
            opt.setCorrect(i == correctIndex);
            optionRepository.save(opt);
        }

        if (existing.size() > optionsText.length) {
            optionRepository.deleteAll(existing.subList(optionsText.length, existing.size()));
        }
    }

    private List<String> validateForm(QuestionForm form) {
        List<String> errors = new ArrayList<>();

        if (clean(form.statement) == null) {
            errors.add("El enunciado es obligatorio.");
        }

        if (form.categoryId == null || resolveCategoryName(form.categoryId) == null) {
            errors.add("La categoría es obligatoria.");
        }

        if (form.correctIndex < 0 || form.correctIndex > 3) {
            errors.add("La respuesta correcta es obligatoria.");
        }

        if (clean(form.option1) == null || clean(form.option2) == null || clean(form.option3) == null || clean(form.option4) == null) {
            errors.add("Las 4 opciones son obligatorias.");
        }

        return errors;
    }

    private String clean(String s) {
        if (s == null) return null;
        String trimmed = s.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String resolveCategoryName(Long categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findById(categoryId)
            .filter(CategoryEntity::isActive)
            .map(CategoryEntity::getName)
            .orElse(null);
    }

    public static final class QuestionForm {
        private String statement;
        private Long categoryId;
        private Difficulty difficulty;
        private boolean active;
        private String option1;
        private String option2;
        private String option3;
        private String option4;
        private int correctIndex;

        public QuestionForm() {
            this.difficulty = Difficulty.EASY;
            this.active = true;
            this.correctIndex = 0;
        }

        public QuestionForm(String statement, Long categoryId, Difficulty difficulty, boolean active,
                            String option1, String option2, String option3, String option4, int correctIndex) {
            this.statement = statement;
            this.categoryId = categoryId;
            this.difficulty = difficulty;
            this.active = active;
            this.option1 = option1;
            this.option2 = option2;
            this.option3 = option3;
            this.option4 = option4;
            this.correctIndex = correctIndex;
        }

        public static QuestionForm empty() {
            return new QuestionForm("", null, Difficulty.EASY, true, "", "", "", "", 0);
        }

        public static QuestionForm from(QuestionEntity q, List<QuestionOptionEntity> options, Long categoryId) {
            String[] values = {"", "", "", ""};
            int correct = 0;
            for (int i = 0; i < options.size() && i < 4; i++) {
                values[i] = options.get(i).getOptionText();
                if (options.get(i).isCorrect()) {
                    correct = i;
                }
            }
            return new QuestionForm(
                q.getStatement(),
                categoryId,
                q.getDifficulty(),
                q.isActive(),
                values[0],
                values[1],
                values[2],
                values[3],
                correct
            );
        }

        public String getStatement() {
            return statement;
        }

        public Long getCategoryId() {
            return categoryId;
        }

        public Difficulty getDifficulty() {
            return difficulty;
        }

        public boolean isActive() {
            return active;
        }

        public String getOption1() {
            return option1;
        }

        public String getOption2() {
            return option2;
        }

        public String getOption3() {
            return option3;
        }

        public String getOption4() {
            return option4;
        }

        public int getCorrectIndex() {
            return correctIndex;
        }
    }
}
