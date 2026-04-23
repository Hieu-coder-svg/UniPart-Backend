package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.request.JobCreationRequest;
import com.unipart.unipart_backend.dto.request.JobFilterRequest;
import com.unipart.unipart_backend.dto.request.JobUpdateRequest;
import com.unipart.unipart_backend.dto.response.JobResponse;
import com.unipart.unipart_backend.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

public interface JobService  {
    JobResponse createJob(JobCreationRequest request);
    JobResponse updateJob(Long jobId, JobUpdateRequest request);
    JobResponse getJobDetail(Long jobId);
    List<JobResponse> getMyJobPost();
    Page<JobResponse> getAllJobs(JobFilterRequest request);
    List<JobResponse> getStudentJobHistory(String studentId);
}
