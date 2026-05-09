package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.Application;
import com.unipart.unipart_backend.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByJob_EmployerId(String employerId);

    long countByJobIdAndStatus(Long jobId, ApplicationStatus status);

    Optional<Application> findByStudentIdAndJobId(String studentId, Long jobId);

    Optional<Application> findByStudentIdAndJobIdAndStatus(
            String studentId, Long jobId, ApplicationStatus status);

    @Query("""
        SELECT a FROM Application a
        JOIN FETCH a.job j
        JOIN FETCH a.student s
        JOIN FETCH s.user u
        WHERE j.employerId = :employerId
    """)
    List<Application> findAllByEmployerIdWithDetails(String employerId);

    List<Application> findByStudentId(String studentId);
}