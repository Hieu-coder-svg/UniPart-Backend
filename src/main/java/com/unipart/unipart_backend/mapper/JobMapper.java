package com.unipart.unipart_backend.mapper;

import com.unipart.unipart_backend.dto.request.JobCreationRequest;
import com.unipart.unipart_backend.dto.request.JobUpdateRequest;
import com.unipart.unipart_backend.dto.response.JobResponse;
import com.unipart.unipart_backend.entity.Job;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JobMapper {
    Job toJob(JobCreationRequest job);
    JobResponse toJobResponse(Job job);
    void updateJob(@MappingTarget Job job, JobUpdateRequest request);
    List<JobResponse> toJobResponseList(List<Job> jobs);
}
