package pe.alfinbanco.gestorexamen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import java.util.List;
import pe.alfinbanco.gestorexamen.entity.UserEntity;
import pe.alfinbanco.gestorexamen.repository.UserRepository;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsService userDetailsService(UserRepository repo) {
        return username -> {
        UserEntity u = repo.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (!u.isActive()) throw new IllegalArgumentException("Usuario inactivo");

        return new User(
            u.getUsername(),
            u.getPasswordHash(),
            List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name()))
        );
        };
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (req, res, auth) -> {
        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        res.sendRedirect(isAdmin ? "/admin/dashboard" : "/user/dashboard");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                            AuthenticationSuccessHandler handler) throws Exception {
        http
            .authorizeHttpRequests(a -> a
                .requestMatchers("/login", "/register", "/error", "/css/**", "/js/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(f -> f
                .loginPage("/login")
                .successHandler(handler)
                .permitAll()
            )
            .logout(l -> l
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
            )
            .csrf(Customizer.withDefaults());

        return http.build();
    }
}
