package com.unipart.unipart_backend.mapper;

import com.unipart.unipart_backend.dto.request.JobCreationRequest;
import com.unipart.unipart_backend.dto.request.JobTimeSlotRequest;
import com.unipart.unipart_backend.dto.request.JobUpdateRequest;
import com.unipart.unipart_backend.dto.response.JobResponse;
import com.unipart.unipart_backend.dto.response.JobTimeSlotResponse;
import com.unipart.unipart_backend.entity.Job;
import com.unipart.unipart_backend.entity.JobTimeSlot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface JobMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "jobTimeSlots", ignore = true)
    Job toJobEntity(JobCreationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "jobId", ignore = true)
    @Mapping(target = "job", ignore = true)
    JobTimeSlot toTimeSlotEntity(JobTimeSlotRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "jobTimeSlots", ignore = true)
    void updateJobFromRequest(JobUpdateRequest request, @MappingTarget Job job);

    @Mapping(target = "employerName", source = "employer.user.fullName")
    @Mapping(target = "timeSlots", source = "jobTimeSlots")
    JobResponse toJobResponse(Job job);

    List<JobResponse> toJobResponseList(List<Job> job);

    List<JobTimeSlotResponse> toTimeSlotResponses(List<JobTimeSlot> timeSlots);
}