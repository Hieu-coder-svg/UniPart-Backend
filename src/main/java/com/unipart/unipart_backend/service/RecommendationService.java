package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.response.JobRecommendationResponse;

import java.util.List;

public interface RecommendationService {
    List<JobRecommendationResponse> getRecommendedJobsForStudent(String studentId);
}
