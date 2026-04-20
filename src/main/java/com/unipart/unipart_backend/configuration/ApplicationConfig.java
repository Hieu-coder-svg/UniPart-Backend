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
        return application -> {
            if(userRepository.findByUsername("admin").isEmpty()){
                var role = roleRepository.findById(1).orElseThrow();
                User user = User.builder()
                       .username("admin")
                       .passwordHash(passwordEncoder.encode("Admin123"))
                       .role(role)
                       .build();

               userRepository.save(user);
               log.warn(user.getUsername());
            };
            if(userRepository.findByUsername("student").isEmpty()){
                var roles = roleRepository.findById(2).orElseThrow();
                User user = User.builder()
                        .username("student")
                        .passwordHash(passwordEncoder.encode("Student123"))
                        .role(roles)
                        .build();

                userRepository.save(user);
                log.warn(user.getUsername());
            };
            if(userRepository.findByUsername("employer").isEmpty()){
                var rolee = roleRepository.findById(3).orElseThrow();
                User user = User.builder()
                        .username("employer")
                        .passwordHash(passwordEncoder.encode("Employer123"))
                        .role(rolee)
                        .build();

                userRepository.save(user);
                log.warn(user.getUsername());
            };
        };
    }

}
