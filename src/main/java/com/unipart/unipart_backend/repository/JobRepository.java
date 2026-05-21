package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job,Long>, JpaSpecificationExecutor<Job> {
    Job getById(long id);
    List<Job> findAllByEmployerId(String employerId);

    List<Job> findByIsHideFalseAndExpiredAtAfter(java.time.LocalDateTime now);

    long countByIsHideFalseAndExpiredAtAfter(java.time.LocalDateTime now);
}
