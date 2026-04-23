package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedJobRepository extends JpaRepository<SavedJob,Long> {
    Optional<SavedJob> findByStudentIdAndJobId(String studentId, Long jobId);
    List<SavedJob> findByStudentId(String studentId);
}
