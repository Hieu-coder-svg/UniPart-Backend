package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.request.SavedJobRequest;
import com.unipart.unipart_backend.dto.response.SavedJobResponse;

import java.util.List;

public interface SavedJobService {
    SavedJobResponse saveJob(SavedJobRequest request);
    void unsaveJob(String studentId, Long jobId);
    List<SavedJobResponse> getMySavedJobsByStudentId();

    boolean isJobSaved(Long jobId);
}
