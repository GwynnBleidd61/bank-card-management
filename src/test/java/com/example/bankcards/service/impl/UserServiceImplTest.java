package com.example.bankcards.service.impl;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        userService = new UserServiceImpl(userRepository, roleRepository, passwordEncoder);
    }

    @Test
    void ensureAdminUser_createsUserWithAdminRoleWhenMissing() {
        Role role = new Role();
        role.setName("ROLE_ADMIN");

        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(role));
        when(userRepository.findByUsername("admin@test")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encoded");

        userService.ensureAdminUser();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();

        assertEquals("admin@test", saved.getUsername());
        assertEquals("encoded", saved.getPassword());
        assertEquals("Default Admin", saved.getFullName());
        assertTrue(saved.isEnabled());
        assertEquals(1, saved.getRoles().size());
        assertTrue(saved.getRoles().contains(role));
    }

    @Test
    void ensureAdminUser_updatesExistingUser() {
        Role role = new Role();
        role.setName("ROLE_ADMIN");

        User existing = new User();
        existing.setUsername("admin@test");
        existing.setRoles(new HashSet<>());

        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(role));
        when(userRepository.findByUsername("admin@test")).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("password")).thenReturn("new-encoded");

        userService.ensureAdminUser();

        verify(userRepository).save(existing);
        assertEquals("new-encoded", existing.getPassword());
        assertTrue(existing.getRoles().contains(role));
    }
}
