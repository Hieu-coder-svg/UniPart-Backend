package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.request.ApplyJobRequest;
import com.unipart.unipart_backend.dto.request.ApplyJobUpdateRequest;
import com.unipart.unipart_backend.dto.response.ApplicationResponse;

public interface ApplicationJobService {
    ApplicationResponse applyJob(ApplyJobRequest request);
    void deleteApplicationJob(Long id);
    ApplicationResponse changeStatus(ApplyJobUpdateRequest request);
}
