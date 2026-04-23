package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.request.SavedJobRequest;
import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.dto.response.SavedJobResponse;
import com.unipart.unipart_backend.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/saved-jobs")
@RequiredArgsConstructor
public class SavedJobController {

    private final SavedJobService savedJobService;

    @PostMapping
    public ApiResponse<SavedJobResponse> saveJob(@RequestBody SavedJobRequest request) {
        return ApiResponse.<SavedJobResponse>builder()
                .result(savedJobService.saveJob(request))
                .build();
    }

    @DeleteMapping("/{jobId}")
    public ApiResponse<String> unsaveJob(@PathVariable Long jobId) {
        return ApiResponse.<String>builder()
                .result("success")
                .build();
    }

    @GetMapping()
    public ApiResponse<List<SavedJobResponse>> getSavedJobs() {
        List<SavedJobResponse> savedJobs = savedJobService.getMySavedJobsByStudentId();
        return ApiResponse.<List<SavedJobResponse>>builder()
                .result(savedJobs)
                .build();
    }

    @GetMapping("/check/{jobId}")
    public ApiResponse<Boolean> isJobSaved(@PathVariable Long jobId) {
        boolean isSaved = savedJobService.isJobSaved(jobId);
        return ApiResponse.<Boolean>builder()
                .result(isSaved)
                .build();
    }
}