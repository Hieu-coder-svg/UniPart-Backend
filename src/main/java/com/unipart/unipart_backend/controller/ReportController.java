package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.request.ReportRequest;
import com.unipart.unipart_backend.dto.request.ReportUpdateRequest;
import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.dto.response.ReportResponse;
import com.unipart.unipart_backend.enums.ReportStatus;
import com.unipart.unipart_backend.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    // ===== User endpoints (Student / Employer) =====

    /**
     * Tạo report mới. Cả Student và Employer đều có quyền.
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('EMPLOYER')")
    public ApiResponse<ReportResponse> createReport(@Valid @RequestBody ReportRequest request) {
        return ApiResponse.<ReportResponse>builder()
                .result(reportService.createReport(request))
                .build();
    }

    /**
     * Xem danh sách report của chính mình.
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT') or hasRole('EMPLOYER')")
    public ApiResponse<List<ReportResponse>> getMyReports() {
        return ApiResponse.<List<ReportResponse>>builder()
                .result(reportService.getMyReports())
                .build();
    }

    // ===== Admin endpoints =====

    /**
     * Lấy tất cả report (tuỳ chọn lọc theo status).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<ReportResponse>> getAllReports(
            @RequestParam(required = false) ReportStatus status) {

        List<ReportResponse> result = (status != null)
                ? reportService.getReportsByStatus(status)
                : reportService.getAllReports();

        return ApiResponse.<List<ReportResponse>>builder()
                .result(result)
                .build();
    }

    /**
     * Xem chi tiết một report.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ReportResponse> getReportById(@PathVariable Long id) {
        return ApiResponse.<ReportResponse>builder()
                .result(reportService.getReportById(id))
                .build();
    }

    /**
     * Admin cập nhật trạng thái report (REVIEWING / RESOLVED / REJECTED).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ReportResponse> updateReport(
            @PathVariable Long id,
            @Valid @RequestBody ReportUpdateRequest request) {
        return ApiResponse.<ReportResponse>builder()
                .result(reportService.updateReportStatus(id, request))
                .build();
    }
}
