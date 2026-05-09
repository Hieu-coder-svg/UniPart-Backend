package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.BackupHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BackupHistoryRepository extends JpaRepository<BackupHistory, Long> {
    List<BackupHistory> findAllByOrderByDateDesc();
}
