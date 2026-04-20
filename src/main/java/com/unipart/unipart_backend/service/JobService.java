package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.request.JobCreationRequest;
import com.unipart.unipart_backend.dto.request.JobUpdateRequest;
import com.unipart.unipart_backend.dto.response.JobResponse;
import com.unipart.unipart_backend.entity.Job;
import org.springframework.stereotype.Service;

import java.util.List;

public interface JobService  {
     JobResponse createJob(JobCreationRequest job);
     JobResponse updateJob(long job , JobUpdateRequest request);
     List<JobResponse> getMyJob(Long id);
     JobResponse getJobDetails(Long id);
     List<Job> getAll();
}
