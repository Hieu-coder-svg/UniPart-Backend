package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.request.JobCreationRequest;
import com.unipart.unipart_backend.dto.request.JobFilterRequest;
import com.unipart.unipart_backend.dto.request.JobUpdateRequest;
import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.dto.response.JobResponse;
import com.unipart.unipart_backend.entity.Job;
import com.unipart.unipart_backend.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/job")
public class JobController {
    @Autowired
    JobService jobService;
    @PostMapping
    ApiResponse<JobResponse> createJob(@RequestBody JobCreationRequest request){
        return ApiResponse.<JobResponse>builder()
                .result(jobService.createJob(request))
                .build();
    }
    @GetMapping
    ApiResponse<Page<JobResponse>> getAllJob(@RequestBody JobFilterRequest request){
        return ApiResponse.<Page<JobResponse>>builder()
                .result(jobService.getAllJobs(request))
                .build();
    }
    @PutMapping("/{id}")
    ApiResponse<JobResponse> updateJob(@PathVariable long id, @RequestBody JobUpdateRequest request){
        return ApiResponse.<JobResponse>builder()
                .result(jobService.updateJob(id,request))
                .build();
    }
    @GetMapping("/myPost")
    ApiResponse<List<JobResponse>> getMyJobPost(){
        return ApiResponse.<List<JobResponse>>builder()
                .result(jobService.getMyJobPost())
                .build();
    }
    @GetMapping("/{id}")
    ApiResponse<JobResponse> getJobDetails(@PathVariable long id){
        return ApiResponse.<JobResponse>builder()
                .result(jobService.getJobDetail(id))
                .build();
    }
    @GetMapping("/history/{id}")
    ApiResponse<List<JobResponse>> getJobHistory(@PathVariable String id){
        return ApiResponse.<List<JobResponse>>builder()
                .result(jobService.getStudentJobHistory(id))
                .build();
    }
}
