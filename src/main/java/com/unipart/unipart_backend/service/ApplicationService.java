package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.response.ApplicationResponse;

import java.util.List;

public interface ApplicationService {

    List<ApplicationResponse> getEmployerApplications();

    ApplicationResponse acceptApplication(Long id);

    ApplicationResponse rejectApplication(Long id);
}