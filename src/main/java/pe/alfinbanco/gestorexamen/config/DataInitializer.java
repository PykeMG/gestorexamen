package pe.alfinbanco.gestorexamen.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import pe.alfinbanco.gestorexamen.entity.*;
import pe.alfinbanco.gestorexamen.repository.*;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner init(UserRepository uRepo,
                            CategoryRepository cRepo,
                            QuestionRepository qRepo,
                            QuestionOptionRepository oRepo,
                            PasswordEncoder enc) {

        return args -> {
        if (uRepo.count() == 0) {
            uRepo.save(new UserEntity("admin", enc.encode("admin123"), Role.ADMIN));
            uRepo.save(new UserEntity("user", enc.encode("user123"), Role.USER));
        }

        if (cRepo.count() == 0) {
            cRepo.save(new CategoryEntity("Matemática", true));
            cRepo.save(new CategoryEntity("Web", true));
            cRepo.save(new CategoryEntity("Java", true));
            cRepo.save(new CategoryEntity("Estructuras", true));
        }

        if (qRepo.count() == 0) {
            create(qRepo, oRepo, "¿Cuál es el resultado de 7 + 5?", "Matemática", Difficulty.EASY,
                List.of("10", "12", "13", "14"), 1);

            create(qRepo, oRepo, "¿Qué HTTP status code significa 'Not Found'?", "Web", Difficulty.EASY,
                List.of("200", "301", "404", "500"), 2);

            create(qRepo, oRepo, "En Java, ¿qué palabra reservada se usa para heredar de una clase?", "Java", Difficulty.EASY,
                List.of("implements", "extends", "inherit", "super"), 1);

            create(qRepo, oRepo, "¿Cuál estructura NO permite duplicados?", "Estructuras", Difficulty.MEDIUM,
                List.of("ArrayList", "HashSet", "LinkedList", "Vector"), 1);

            create(qRepo, oRepo, "Salida de: System.out.println(3 * 2 + 1)?", "Java", Difficulty.EASY,
                List.of("7", "9", "8", "5"), 0);
        }
        };
    }

    private void create(QuestionRepository qRepo,
                        QuestionOptionRepository oRepo,
                        String statement,
                        String category,
                        Difficulty difficulty,
                        List<String> options,
                        int correctIdx) {

        QuestionEntity q = qRepo.save(new QuestionEntity(statement, category, difficulty));
        for (int i = 0; i < options.size(); i++) {
        oRepo.save(new QuestionOptionEntity(q, options.get(i), i == correctIdx));
        }
    }
}
