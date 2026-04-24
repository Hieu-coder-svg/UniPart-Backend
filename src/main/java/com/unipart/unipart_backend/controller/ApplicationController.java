package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.request.ApplyJobRequest;
import com.unipart.unipart_backend.dto.request.ApplyJobUpdateRequest;
import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.dto.response.ApplicationResponse;
import com.unipart.unipart_backend.service.ApplicationJobService;
import com.unipart.unipart_backend.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final ApplicationJobService applicationJobService;

    // --- API for EMPLOYER ---

    @GetMapping("/employer/applications")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ApiResponse<List<ApplicationResponse>> getApplications() {
        return ApiResponse.<List<ApplicationResponse>>builder()
                .result(applicationService.getEmployerApplications())
                .build();
    }

    @PutMapping("/employer/applications/{id}/accept")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ApiResponse<ApplicationResponse> accept(@PathVariable Long id) {
        return ApiResponse.<ApplicationResponse>builder()
                .result(applicationService.acceptApplication(id))
                .build();
    }

    @PutMapping("/employer/applications/{id}/reject")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ApiResponse<ApplicationResponse> reject(@PathVariable Long id) {
        return ApiResponse.<ApplicationResponse>builder()
                .result(applicationService.rejectApplication(id))
                .build();
    }

    // --- API for APPLICATION JOB (Student) ---

    @PostMapping("/application")
    public ApiResponse<ApplicationResponse> applyJob(@RequestBody ApplyJobRequest request){
        return ApiResponse.<ApplicationResponse>builder()
                .result(applicationJobService.applyJob(request))
                .build();
    }

    @DeleteMapping("/application")
    public ApiResponse<String> deleteApplyJob(@RequestParam long applicationId){
        applicationJobService.deleteApplicationJob(applicationId);
        return ApiResponse.<String>builder()
                .result("Application has been deleted")
                .build();
    }

    @PutMapping("/application")
    public ApiResponse<ApplicationResponse> changeStatusApplyJob(@RequestBody ApplyJobUpdateRequest request){
        return ApiResponse.<ApplicationResponse>builder()
                .result(applicationJobService.changeStatus(request))
                .build();
    }
}