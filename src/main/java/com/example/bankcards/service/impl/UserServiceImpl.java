package com.example.bankcards.service.impl;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void ensureAdminUser() {
        String adminUsername = "admin@test";
        String rawPassword = "password";

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found"));

        User user = userRepository.findByUsername(adminUsername)
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername(adminUsername);
                    u.setCreatedAt(LocalDateTime.now());
                    u.setEnabled(true);
                    return u;
                });

        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setFullName("Default Admin");
        user.setUpdatedAt(LocalDateTime.now());
        user.setEnabled(true);

        var roles = new HashSet<Role>();
        roles.add(adminRole);
        user.setRoles(roles);

        userRepository.save(user);
    }
}
