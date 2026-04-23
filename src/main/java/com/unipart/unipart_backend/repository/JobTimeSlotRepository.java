package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.JobTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobTimeSlotRepository extends JpaRepository<JobTimeSlot, Integer> {
    void deleteByJobId(Long jobId);
}
