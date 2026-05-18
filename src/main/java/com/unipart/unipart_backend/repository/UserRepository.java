package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.dto.response.UserResponse;
import com.unipart.unipart_backend.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findById(String id);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    @EntityGraph(attributePaths = {"role", "student", "employer"})
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    long countByIsBlockedFalse();
    java.util.List<User> findByRole_Name(String roleName);
}
