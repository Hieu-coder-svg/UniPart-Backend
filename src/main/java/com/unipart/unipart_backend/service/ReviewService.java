package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.request.ReviewRequest;
import com.unipart.unipart_backend.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse studentReviewEmployer(ReviewRequest request);

    ReviewResponse employerReviewStudent(ReviewRequest request);

    List<ReviewResponse> getEmployerReviews(String employerId);

    List<ReviewResponse> getStudentReviews(String studentId);
}