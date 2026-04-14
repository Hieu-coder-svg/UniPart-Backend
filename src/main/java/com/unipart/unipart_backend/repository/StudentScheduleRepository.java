package com.unipart.unipart_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentSchedule extends JpaRepository<StudentSchedule,Long> {
}
