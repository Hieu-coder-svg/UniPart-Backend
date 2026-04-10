package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.request.JobCreationRequest;
import com.unipart.unipart_backend.dto.request.JobUpdateRequest;
import com.unipart.unipart_backend.dto.response.JobResponse;
import com.unipart.unipart_backend.entity.Job;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface JobService  {
    public JobResponse createJob(JobCreationRequest job);
    public JobResponse updateJob(long job , JobUpdateRequest request);
    public List<JobResponse> getMyJob(Long id);
    public JobResponse getJobDetails(Long id);
    public List<Job> getAll();
}
