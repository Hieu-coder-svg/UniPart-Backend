package com.unipart.unipart_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {
    private long totalUsers;
    private long activeUsers;
    private long totalStudents;
    private long totalEmployers;
    private long totalJobs;
    private long activeJobs;
    private long totalRequests;
    private long totalPosts;
    private long totalReports;
    private long pendingReports;
    private long resolvedReports;
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
}
