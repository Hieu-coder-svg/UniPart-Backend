package com.unipart.unipart_backend.configuration;

import com.unipart.unipart_backend.entity.Role;
import com.unipart.unipart_backend.entity.User;
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
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return application -> {
            if(userRepository.findByUsername("admin").isEmpty()){
                var role = new Role();
                role.setName("ADMIN");
               User user = User.builder()
                       .username("admin")
                       .passwordHash(passwordEncoder.encode("Admin123"))
                       .role(role)
                       .build();

               userRepository.save(user);
               log.warn(user.getUsername());
            };
            if(userRepository.findByUsername("student").isEmpty()){
                var role = new Role();
                role.setName("STUDENT");
                User user = User.builder()
                        .username("student")
                        .passwordHash(passwordEncoder.encode("Student123"))
                        .role(role)
                        .build();

                userRepository.save(user);
                log.warn(user.getUsername());
            };
            if(userRepository.findByUsername("employer").isEmpty()){
                var role = new Role();
                role.setName("EMPLOYER");
                User user = User.builder()
                        .username("employer")
                        .passwordHash(passwordEncoder.encode("Employer123"))
                        .role(role)
                        .build();

                userRepository.save(user);
                log.warn(user.getUsername());
            };
        };
    }

}
