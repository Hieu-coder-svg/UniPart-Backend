package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.request.ApplyJobRequest;
import com.unipart.unipart_backend.dto.request.ApplyJobUpdateRequest;
import com.unipart.unipart_backend.dto.response.ApplicationResponse;

import java.util.List;

public interface ApplicationJobService {
    ApplicationResponse applyJob(ApplyJobRequest request);
    void deleteApplicationJob(Long id);
    ApplicationResponse changeStatus(ApplyJobUpdateRequest request);
    List<ApplicationResponse> getStudentApplications();
}
