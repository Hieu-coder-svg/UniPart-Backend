package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.Student;
import com.unipart.unipart_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {
    Student findByUser(User user);
    @Query("SELECT s FROM Student s JOIN FETCH s.user u JOIN FETCH u.role WHERE u.username = :username")
    Optional<Student> findByUsernameWithUser(@Param("username") String username);
}
