package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.request.ReviewRequest;
import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.dto.response.ReviewResponse;
import com.unipart.unipart_backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    // 🎓 student → employer
    @PostMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<ReviewResponse> studentReview(@RequestBody ReviewRequest req) {
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.studentReviewEmployer(req))
                .build();
    }

    // 👨‍💼 employer → student
    @PostMapping("/employer")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ApiResponse<ReviewResponse> employerReview(@RequestBody ReviewRequest req) {
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.employerReviewStudent(req))
                .build();
    }

    @GetMapping("/employer/{id}")
    public ApiResponse<List<ReviewResponse>> getEmployer(@PathVariable String id) {
        return ApiResponse.<List<ReviewResponse>>builder()
                .result(reviewService.getEmployerReviews(id))
                .build();
    }

    @GetMapping("/student/{id}")
    public ApiResponse<List<ReviewResponse>> getStudent(@PathVariable String id) {
        return ApiResponse.<List<ReviewResponse>>builder()
                .result(reviewService.getStudentReviews(id))
                .build();
    }
}