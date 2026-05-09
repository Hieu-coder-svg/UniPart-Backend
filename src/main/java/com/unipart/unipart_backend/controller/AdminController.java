package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.response.AdminStatsResponse;
import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.repository.ApplicationRepository;
import com.unipart.unipart_backend.repository.JobRepository;
import com.unipart.unipart_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AdminStatsResponse> getStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsBlockedFalse();
        long totalJobs = jobRepository.count();
        long totalRequests = applicationRepository.count();

        AdminStatsResponse stats = AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalJobs(totalJobs)
                .totalRequests(totalRequests)
                .build();

        return ApiResponse.<AdminStatsResponse>builder()
                .result(stats)
                .build();
    }
}
