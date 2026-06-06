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
    @EntityGraph(attributePaths = {"role", "student", "employer"})
    Optional<User> findById(String id);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    @EntityGraph(attributePaths = {"role", "student", "employer"})
    Optional<User> findByUsername(String username);
    @EntityGraph(attributePaths = {"role", "student", "employer"})
    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findByEmail(String email);
    Optional<User> findFirstByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, String id);
    long countByIsBlockedFalse();
    long countByRole_Name(String roleName);
    java.util.List<User> findByRole_Name(String roleName);
}
