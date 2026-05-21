package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.Report;
import com.unipart.unipart_backend.enums.ReportStatus;
import com.unipart.unipart_backend.enums.ReportTargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByReporterIdOrderByCreatedAtDesc(String reporterId);

    List<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    List<Report> findByTargetTypeAndTargetIdOrderByCreatedAtDesc(
            ReportTargetType targetType, String targetId);

    List<Report> findAllByOrderByCreatedAtDesc();

    boolean existsByReporterIdAndTargetTypeAndTargetId(
            String reporterId, ReportTargetType targetType, String targetId);

    long countByStatus(ReportStatus status);
}
