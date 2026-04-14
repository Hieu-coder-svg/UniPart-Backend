package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.request.JobCreationRequest;
import com.unipart.unipart_backend.dto.request.JobUpdateRequest;
import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.dto.response.JobResponse;
import com.unipart.unipart_backend.entity.Job;
import com.unipart.unipart_backend.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/{job}")
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
    ApiResponse<List<Job>> getAllJob(){
        return ApiResponse.<List<Job>>builder()
                .result(jobService.getAll())
                .build();
    }
    @PutMapping("/{id}")
    ApiResponse<JobResponse> updateJob(@PathVariable long id, @RequestBody JobUpdateRequest request){
        return ApiResponse.<JobResponse>builder()
                .result(jobService.updateJob(id,request))
                .build();
    }
    @GetMapping("/myPost/{id}")
    ApiResponse<List<JobResponse>> getMyJobPost(@PathVariable long id){
        return ApiResponse.<List<JobResponse>>builder()
                .result(jobService.getMyJob(id))
                .build();
    }
    @GetMapping("/{id}")
    ApiResponse<JobResponse> getJobDetails(@PathVariable long id){
        return ApiResponse.<JobResponse>builder()
                .result(jobService.getJobDetails(id))
                .build();
    }
}
