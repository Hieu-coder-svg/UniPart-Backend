package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.request.ApplyJobRequest;
import com.unipart.unipart_backend.dto.request.ApplyJobUpdateRequest;
import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.dto.response.ApplicationResponse;
import com.unipart.unipart_backend.service.ApplicationJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/application")
public class ApplicationController {
    private ApplicationJobService applicationJobService;
    @PostMapping
    public ApiResponse<ApplicationResponse> applyJob(@RequestBody ApplyJobRequest request){
        return ApiResponse.<ApplicationResponse>builder()
                .result(applicationJobService.applyJob(request))
                .build();
    }
    @DeleteMapping
    public ApiResponse<String> deleteApplyJob(@RequestParam long applicationId){
        applicationJobService.deleteApplicationJob(applicationId);
        return ApiResponse.<String>builder()
                .result("Application has been deleted")
                .build();
    }
    @PutMapping
    public ApiResponse<ApplicationResponse> changeStatusApplyJob(@RequestBody ApplyJobUpdateRequest request){
        return ApiResponse.<ApplicationResponse>builder()
                .result(applicationJobService.changeStatus(request))
                .build();
    }
}
