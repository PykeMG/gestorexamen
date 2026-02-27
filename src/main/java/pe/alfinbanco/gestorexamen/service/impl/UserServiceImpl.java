package pe.alfinbanco.gestorexamen.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.alfinbanco.gestorexamen.entity.Role;
import pe.alfinbanco.gestorexamen.entity.UserEntity;
import pe.alfinbanco.gestorexamen.repository.UserRepository;
import pe.alfinbanco.gestorexamen.service.UserService;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntity getByUsernameOrThrow(String username) {
        return repo.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    @Override
    public UserEntity register(String username, String rawPassword) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        if (rawPassword == null || rawPassword.isBlank() || rawPassword.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
        String normalized = username.trim();
        if (repo.existsByUsername(normalized)) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        UserEntity u = new UserEntity();
        u.setUsername(normalized);
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        u.setRole(Role.USER);
        u.setActive(true);
        return repo.save(u);
    }

    @Override
    public UserEntity updateByAdmin(Long id, String username, Role role, boolean active, String rawPasswordOrNull) {
        UserEntity existing = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        String normalized = username.trim();
        if (repo.existsByUsernameAndIdNot(normalized, id)) {
            throw new IllegalArgumentException("El usuario ya existe");
        }
        if (role == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }

        existing.setUsername(normalized);
        existing.setRole(role);
        existing.setActive(active);

        if (rawPasswordOrNull != null && !rawPasswordOrNull.isBlank()) {
            if (rawPasswordOrNull.length() < 6) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
            }
            existing.setPasswordHash(passwordEncoder.encode(rawPasswordOrNull));
        }

        return repo.save(existing);
    }

    @Override
    public List<UserEntity> listAll() {
        return repo.findAll();
    }

    @Override
    public UserEntity save(UserEntity u) {
        return repo.save(u);
    }

    @Override
    public UserEntity getByIdOrThrow(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
}
