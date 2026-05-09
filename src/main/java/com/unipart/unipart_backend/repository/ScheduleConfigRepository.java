package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.ScheduleConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleConfigRepository extends JpaRepository<ScheduleConfig, Long> {
}
