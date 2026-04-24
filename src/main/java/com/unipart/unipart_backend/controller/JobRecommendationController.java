package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.dto.response.JobRecommendationResponse;
import com.unipart.unipart_backend.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/student/recommendations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class JobRecommendationController {

    private final RecommendationService recommendationService;

    private String getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("userId");
        }
        return authentication.getName();
    }

    @GetMapping
    public ApiResponse<List<JobRecommendationResponse>> getRecommendations() {
        String studentId = getCurrentUserId();
        return ApiResponse.<List<JobRecommendationResponse>>builder()
                .result(recommendationService.getRecommendedJobsForStudent(studentId))
                .build();
    }
}
