package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.Student;
import com.unipart.unipart_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;

public interface StudentRepository extends JpaRepository<Student, String> {
    Student findByUser(User user);
}
