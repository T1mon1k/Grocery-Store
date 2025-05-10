package com.example.onlinestore.service;

import com.example.onlinestore.dto.UserDto;
import com.example.onlinestore.entity.Role;
import com.example.onlinestore.entity.User;
import com.example.onlinestore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepo;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedpass")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    void registerNew_shouldSaveNewUser() {
        UserDto dto = new UserDto();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setEmail("test@example.com");

        when(userRepo.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedpass");
        when(userRepo.save(any())).thenReturn(user);

        User saved = userService.registerNew(dto);

        assertNotNull(saved);
        assertEquals("testuser", saved.getUsername());
        verify(userRepo).save(any());
    }

    @Test
    void registerNew_shouldThrowIfUsernameTaken() {
        UserDto dto = new UserDto();
        dto.setUsername("testuser");
        dto.setPassword("password123");
        dto.setEmail("test@example.com");

        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> userService.registerNew(dto));
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails details = userService.loadUserByUsername("testuser");

        assertNotNull(details);
        assertEquals("testuser", details.getUsername());
        assertEquals("encodedpass", details.getPassword());
    }

    @Test
    void loadUserByUsername_shouldThrowIfNotFound() {
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("unknown"));
    }

    @Test
    void getAllUsers_shouldReturnList() {
        when(userRepo.findAll()).thenReturn(List.of(user));

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    @Test
    void updateRole_shouldUpdateAndSaveUser() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        userService.updateRole(1L, Role.ROLE_ADMIN);

        assertEquals(Role.ROLE_ADMIN, user.getRole());
        verify(userRepo).save(user);
    }

    @Test
    void findById_shouldReturnUserIfExists() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }
}
