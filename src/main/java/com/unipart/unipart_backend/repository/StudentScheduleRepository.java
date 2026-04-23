package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.dto.response.StudentScheduleResponse;
import com.unipart.unipart_backend.entity.StudentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentScheduleRepository extends JpaRepository<StudentSchedule, Integer> {
    List<StudentSchedule> findByUserId(String userId);
    void deleteAllByUserId(String userId);
}
