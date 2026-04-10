package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job,Long> {
    Job getById(long id);
    List<Job> findAllByEmployerId(String employerId);

}
