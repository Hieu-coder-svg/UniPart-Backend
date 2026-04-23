package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.dto.response.ApplicationResponse;
import com.unipart.unipart_backend.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employer/applications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYER')")
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping
    public ApiResponse<List<ApplicationResponse>> getApplications() {
        return ApiResponse.<List<ApplicationResponse>>builder()
                .result(applicationService.getEmployerApplications())
                .build();
    }

    @PutMapping("/{id}/accept")
    public ApiResponse<ApplicationResponse> accept(@PathVariable Long id) {
        return ApiResponse.<ApplicationResponse>builder()
                .result(applicationService.acceptApplication(id))
                .build();
    }

    @PutMapping("/{id}/reject")
    public ApiResponse<ApplicationResponse> reject(@PathVariable Long id) {
        return ApiResponse.<ApplicationResponse>builder()
                .result(applicationService.rejectApplication(id))
                .build();
    }
}