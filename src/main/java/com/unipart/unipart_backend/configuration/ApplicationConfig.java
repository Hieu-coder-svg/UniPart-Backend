package com.unipart.unipart_backend.configuration;

import com.unipart.unipart_backend.entity.Employer;
import com.unipart.unipart_backend.entity.Role;
import com.unipart.unipart_backend.entity.Student;
import com.unipart.unipart_backend.entity.User;
import com.unipart.unipart_backend.repository.EmployerRepository;
import com.unipart.unipart_backend.repository.RoleRepository;
import com.unipart.unipart_backend.repository.StudentRepository;
import com.unipart.unipart_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationConfig {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final EmployerRepository employerRepository;

    @Bean
    @Transactional
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {

            // roles
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ADMIN").build()));

            Role studentRole = roleRepository.findByName("STUDENT")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("STUDENT").build()));

            Role employerRole = roleRepository.findByName("EMPLOYER")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("EMPLOYER").build()));

            if(userRepository.findByUsername("admin").isEmpty()){
                User u = User.builder()
                        .username("admin")
                        .email("admin@unipart.vn")
                        .fullName("Administrator")
                        .passwordHash(passwordEncoder.encode("Admin123"))
                        .role(adminRole)
                        .isActived(true)
                        .isBlocked(false)
                        .build();
                userRepository.save(u);
            }
            if (userRepository.findByUsername("student").isEmpty()) {
                User u = User.builder()
                        .username("student")
                        .email("student@gmail.com")
                        .passwordHash(passwordEncoder.encode("Student123"))
                        .role(studentRole)
                        .fullName("student")
                        .isActived(true)
                        .isBlocked(false)
                        .build();

                Student student = Student.builder()
                        .user(u)
                        .university("FPT University")
                        .major("Software Engineering")
                        .build();
                u.setStudent(student);
                User savedUser = userRepository.save(u);
                student.setUser(savedUser);
                studentRepository.save(student);
            }
             if (userRepository.findByUsername("employer").isEmpty()) {

                 User ue = User.builder()
                         .username("employer")
                         .email("employer@gmail.com")
                         .passwordHash(passwordEncoder.encode("Employer123"))
                         .role(employerRole)
                         .fullName("employer")
                         .isActived(true)
                         .isBlocked(false)
                         .build();

                 Employer employer= Employer.builder()
                         .user(ue)
                         .companyName("ABC Company")
                         .companyAddress("Hanoi")
                         .build();
                 ue.setEmployer(employer);
                 User save = userRepository.save(ue);
                 employer.setUser(save);
                 employerRepository.save(employer);
             }
        };
    }
}