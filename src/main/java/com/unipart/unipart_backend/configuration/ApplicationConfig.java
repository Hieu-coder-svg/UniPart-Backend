package com.unipart.unipart_backend.configuration;

import com.unipart.unipart_backend.entity.Role;
import com.unipart.unipart_backend.entity.User;
import com.unipart.unipart_backend.repository.RoleRepository;
import com.unipart.unipart_backend.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
@Slf4j
public class ApplicationConfig {
    private PasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {

            // roles
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ADMIN").build()));

            Role studentRole = roleRepository.findByName("STUDENT")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("STUDENT").build()));

            Role employerRole = roleRepository.findByName("EMPLOYER")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("EMPLOYER").build()));

            // admin
            if (userRepository.findByUsername("admin").isEmpty()) {
                userRepository.save(User.builder()
                        .username("admin")
                        .email("admin@gmail.com")
                        .passwordHash(passwordEncoder.encode("Admin123"))
                        .role(adminRole)
                        .isActived(true)
                        .isBlocked(false)
                        .build());
            }

            // student
            if (userRepository.findByUsername("student").isEmpty()) {
                userRepository.save(User.builder()
                        .username("student")
                        .email("student@gmail.com")
                        .passwordHash(passwordEncoder.encode("Student123"))
                        .role(studentRole)
                        .isActived(true)
                        .isBlocked(false)
                        .build());
            }

            // employer
            if (userRepository.findByUsername("employer").isEmpty()) {
                userRepository.save(User.builder()
                        .username("employer")
                        .email("employer@gmail.com")
                        .passwordHash(passwordEncoder.encode("Employer123"))
                        .role(employerRole)
                        .isActived(true)
                        .isBlocked(false)
                        .build());
            }
        };

    }

}
