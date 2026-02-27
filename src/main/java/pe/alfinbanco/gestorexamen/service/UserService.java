package pe.alfinbanco.gestorexamen.service;

import java.util.List;
import pe.alfinbanco.gestorexamen.entity.Role;
import pe.alfinbanco.gestorexamen.entity.UserEntity;

public interface UserService {
    UserEntity getByUsernameOrThrow(String username);
    UserEntity register(String username, String rawPassword);
    UserEntity updateByAdmin(Long id, String username, Role role, boolean active, String rawPasswordOrNull);
    List<UserEntity> listAll();
    UserEntity save(UserEntity u);
    UserEntity getByIdOrThrow(Long id);
}
