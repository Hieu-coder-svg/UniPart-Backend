package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.request.ReportRequest;
import com.unipart.unipart_backend.dto.request.ReportUpdateRequest;
import com.unipart.unipart_backend.dto.response.ReportResponse;
import com.unipart.unipart_backend.enums.ReportStatus;

import java.util.List;

public interface ReportService {

    // ===== User (Student / Employer) =====
    ReportResponse createReport(ReportRequest request);

    List<ReportResponse> getMyReports();

    // ===== Admin =====
    List<ReportResponse> getAllReports();

    List<ReportResponse> getReportsByStatus(ReportStatus status);

    ReportResponse getReportById(Long reportId);

    ReportResponse updateReportStatus(Long reportId, ReportUpdateRequest request);
}
