package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.request.NotificationCreationRequest;
import com.unipart.unipart_backend.dto.request.ReportRequest;
import com.unipart.unipart_backend.dto.request.ReportUpdateRequest;
import com.unipart.unipart_backend.dto.response.ReportResponse;
import com.unipart.unipart_backend.entity.Report;
import com.unipart.unipart_backend.entity.User;
import com.unipart.unipart_backend.enums.ReportStatus;
import com.unipart.unipart_backend.enums.ReportTargetType;
import com.unipart.unipart_backend.exception.AppException;
import com.unipart.unipart_backend.exception.ErrorCode;
import com.unipart.unipart_backend.mapper.ReportMapper;
import com.unipart.unipart_backend.repository.JobRepository;
import com.unipart.unipart_backend.repository.ReportRepository;
import com.unipart.unipart_backend.repository.ReviewRepository;
import com.unipart.unipart_backend.repository.UserRepository;
import com.unipart.unipart_backend.service.NotificationService;
import com.unipart.unipart_backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ReviewRepository reviewRepository;
    private final ReportMapper reportMapper;
    private final NotificationService notificationService;

    // ===== Helper =====

    private String getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("userId");
        }
        return authentication.getName();
    }

    private void validateTargetExists(ReportTargetType targetType, String targetId) {
        switch (targetType) {
            case USER -> {
                if (!userRepository.existsById(targetId)) {
                    throw new AppException(ErrorCode.USER_NOT_EXIST);
                }
            }
            case JOB -> {
                Long jobId = parseLongId(targetId, "Job ID");
                if (!jobRepository.existsById(jobId)) {
                    throw new AppException(ErrorCode.JOB_NOT_FOUND);
                }
            }
            case REVIEW -> {
                Long reviewId = parseLongId(targetId, "Review ID");
                if (!reviewRepository.existsById(reviewId)) {
                    throw new AppException(ErrorCode.REPORT_TARGET_NOT_FOUND);
                }
            }
            default -> {
                // POST, COMMENT — admin sẽ xác minh thủ công
            }
        }
    }

    private Long parseLongId(String value, String fieldName) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new AppException(ErrorCode.REPORT_INVALID_TARGET_ID);
        }
    }

    private void sendNotification(String userId, String title, String content) {
        if (userId == null) return;
        try {
            notificationService.createNotification(
                    NotificationCreationRequest.builder()
                            .userId(userId)
                            .title(title)
                            .content(content)
                            .build()
            );
        } catch (Exception e) {
            System.err.println("[ReportService] Failed to send notification: " + e.getMessage());
        }
    }

    // ================= USER: Tạo report =================
    @Override
    @Transactional
    public ReportResponse createReport(ReportRequest request) {

        String userId = getCurrentUserId();
        if (userId == null) throw new AppException(ErrorCode.UNAUTHENTICATED);

        // Không được report chính mình
        if (request.getTargetType() == ReportTargetType.USER
                && request.getTargetId().equals(userId)) {
            throw new AppException(ErrorCode.REPORT_SELF_FORBIDDEN);
        }

        // Kiểm tra trùng lặp
        if (reportRepository.existsByReporterIdAndTargetTypeAndTargetId(
                userId, request.getTargetType(), request.getTargetId())) {
            throw new AppException(ErrorCode.REPORT_ALREADY_EXISTS);
        }

        validateTargetExists(request.getTargetType(), request.getTargetId());

        Report report = Report.builder()
                .reporterId(userId)
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .reason(request.getReason())
                .evidenceUrl(request.getEvidenceUrl())
                .status(ReportStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        reportRepository.save(report);

        // Gửi notification cho tất cả ADMIN
        List<User> admins = userRepository.findByRole_Name("ADMIN");
        for (User admin : admins) {
            sendNotification(admin.getId(),
                    "📢 Báo cáo vi phạm mới",
                    "Có một báo cáo mới về " + request.getTargetType().name().toLowerCase()
                            + " cần được xem xét. Lý do: " + request.getReason());
        }

        User reporter = userRepository.findById(userId).orElse(null);
        ReportResponse response = reportMapper.toResponse(report);
        if (reporter != null) {
            response.setReporterName(reporter.getFullName());
        }
        return response;
    }

    // ================= USER: Xem report của mình =================
    @Override
    public List<ReportResponse> getMyReports() {
        String userId = getCurrentUserId();
        if (userId == null) throw new AppException(ErrorCode.UNAUTHENTICATED);
        return reportMapper.toResponseList(
                reportRepository.findByReporterIdOrderByCreatedAtDesc(userId));
    }

    // ================= ADMIN: Lấy tất cả =================
    @Override
    public List<ReportResponse> getAllReports() {
        return reportMapper.toResponseList(
                reportRepository.findAllByOrderByCreatedAtDesc());
    }

    // ================= ADMIN: Lọc theo status =================
    @Override
    public List<ReportResponse> getReportsByStatus(ReportStatus status) {
        return reportMapper.toResponseList(
                reportRepository.findByStatusOrderByCreatedAtDesc(status));
    }

    // ================= ADMIN: Xem chi tiết =================
    @Override
    public ReportResponse getReportById(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));
        return reportMapper.toResponse(report);
    }

    // ================= ADMIN: Cập nhật trạng thái =================
    @Override
    @Transactional
    public ReportResponse updateReportStatus(Long reportId, ReportUpdateRequest request) {

        String adminId = getCurrentUserId();
        if (adminId == null) throw new AppException(ErrorCode.UNAUTHENTICATED);

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));

        report.setStatus(request.getStatus());
        report.setAdminNote(request.getAdminNote());
        report.setResolvedBy(adminId);
        report.setUpdatedAt(LocalDateTime.now());
        reportRepository.save(report);

        // Gửi notification sau khi giải quyết
        if (request.getStatus() == ReportStatus.RESOLVED || request.getStatus() == ReportStatus.REJECTED) {
            String statusLabel = request.getStatus() == ReportStatus.RESOLVED ? "Đã giải quyết" : "Bị từ chối";

            // → Người báo cáo
            sendNotification(report.getReporterId(),
                    "Báo cáo của bạn đã được xử lý",
                    "Báo cáo #" + reportId + " của bạn có trạng thái: " + statusLabel
                            + (request.getAdminNote() != null && !request.getAdminNote().isBlank()
                            ? ". Ghi chú: " + request.getAdminNote() : ""));

            // → Người bị báo cáo (chỉ khi targetType là USER)
            if (report.getTargetType() == ReportTargetType.USER) {
                sendNotification(report.getTargetId(),
                        "Thông báo từ quản trị viên",
                        "Tài khoản của bạn đã bị báo cáo. Báo cáo đã được xử lý với trạng thái: " + statusLabel);
            }
        }

        return reportMapper.toResponse(report);
    }
}
