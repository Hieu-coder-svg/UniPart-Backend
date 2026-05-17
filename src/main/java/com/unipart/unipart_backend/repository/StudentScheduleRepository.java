package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.dto.response.StudentScheduleResponse;
import com.unipart.unipart_backend.entity.StudentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentScheduleRepository extends JpaRepository<StudentSchedule, Integer> {
    List<StudentSchedule> findByUserId(String userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM StudentSchedule s WHERE s.userId = :userId")
    void deleteAllByUserId(String userId);
}
