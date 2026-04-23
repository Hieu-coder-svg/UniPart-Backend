package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationJobRepository extends JpaRepository<Application,Long> {
    Optional<Application> findById(Long id);

    List<Application> findByStudentId(String studentId);
}
