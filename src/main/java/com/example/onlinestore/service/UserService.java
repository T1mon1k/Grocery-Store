package com.example.onlinestore.service;

import com.example.onlinestore.dto.UserDto;
import com.example.onlinestore.entity.Role;
import com.example.onlinestore.entity.User;
import com.example.onlinestore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    public User registerNew(UserDto dto) {
        if (userRepo.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Ім'я користувача вже зайняте");
        }
        if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Ця електронна пошта вже використовується");
        }
        User u = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.ROLE_USER)
                .build();
        return userRepo.save(u);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.onlinestore.entity.User u = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user"));

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPassword())
                .roles(u.getRole().name().substring(5))
                .build();
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public void updateRole(Long userId, Role role) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
        u.setRole(role);
        userRepo.save(u);
    }

    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }
}
