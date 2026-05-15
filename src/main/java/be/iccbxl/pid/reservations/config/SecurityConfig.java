package be.iccbxl.pid.reservations.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Ressources statiques accessibles à tous
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                // Pages publiques
                .requestMatchers("/", "/login", "/register", "/forgot-password", "/reset-password/**").permitAll()
                // Lecture des artistes accessible à tous (provisoire)
                .requestMatchers("/artists", "/artists/{id:[\\d]+}").permitAll()
                .requestMatchers("/shows", "/shows/**").permitAll() // catalogue public
                // Création/modification/suppression d'artistes réservée aux ADMIN
                .requestMatchers("/artists/new", "/artists/{id:[\\d]+}/edit", "/artists/{id:[\\d]+}/delete").hasRole("ADMIN")
                // Toutes les autres URL nécessitent d'être connecté
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );
           
        return http.build();
    }
}