package com.example.onlinestore.config;

import com.example.onlinestore.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.http.HttpMethod;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // публічні сторінки
                        .requestMatchers("/", "/css/**", "/js/**", "/register", "/login", "/products").permitAll()
                        // оновлення статусу для юзерів і адмінів
                        .requestMatchers(HttpMethod.POST, "/orders/*/status")
                        .hasAnyRole("USER","ADMIN")
                        // повна адмін-панель (якщо є)
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // все інше – для залогінених
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder імплементує org.springframework.security.crypto.password.PasswordEncoder
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider(UserService userService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        // тут викликаємо метод, який приймає PasswordEncoder
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
