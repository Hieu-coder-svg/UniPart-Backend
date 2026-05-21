package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.request.NotificationCreationRequest;
import com.unipart.unipart_backend.dto.response.AdminChartResponse;
import com.unipart.unipart_backend.dto.response.AdminStatsResponse;
import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.entity.User;
import com.unipart.unipart_backend.enums.PaymentStatus;
import com.unipart.unipart_backend.enums.ReportStatus;
import com.unipart.unipart_backend.repository.ApplicationRepository;
import com.unipart.unipart_backend.repository.EmployerPackagePurchaseRepository;
import com.unipart.unipart_backend.repository.JobRepository;
import com.unipart.unipart_backend.repository.PostRepository;
import com.unipart.unipart_backend.repository.ReportRepository;
import com.unipart.unipart_backend.repository.UserRepository;
import com.unipart.unipart_backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private JobRepository jobRepository;
    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private ReportRepository reportRepository;
    @Autowired private EmployerPackagePurchaseRepository purchaseRepository;
    @Autowired private NotificationService notificationService;

    // ===== Stats =====

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AdminStatsResponse> getStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsBlockedFalse();
        long totalStudents = userRepository.countByRole_Name("STUDENT");
        long totalEmployers = userRepository.countByRole_Name("EMPLOYER");

        long totalJobs = jobRepository.count();
        LocalDateTime now = LocalDateTime.now();
        long activeJobs = jobRepository.countByIsHideFalseAndExpiredAtAfter(now);

        long totalRequests = applicationRepository.count();
        long totalPosts = postRepository.count();

        long totalReports = reportRepository.count();
        long pendingReports = reportRepository.countByStatus(ReportStatus.PENDING);
        long resolvedReports = reportRepository.countByStatus(ReportStatus.RESOLVED);

        BigDecimal totalRevenue = purchaseRepository.sumPricePaidByPaymentStatus(PaymentStatus.SUCCESS);
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        BigDecimal monthlyRevenue = purchaseRepository.sumPricePaidByPaymentStatusSince(PaymentStatus.SUCCESS, startOfMonth);
        if (monthlyRevenue == null) monthlyRevenue = BigDecimal.ZERO;

        AdminStatsResponse stats = AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalStudents(totalStudents)
                .totalEmployers(totalEmployers)
                .totalJobs(totalJobs)
                .activeJobs(activeJobs)
                .totalRequests(totalRequests)
                .totalPosts(totalPosts)
                .totalReports(totalReports)
                .pendingReports(pendingReports)
                .resolvedReports(resolvedReports)
                .totalRevenue(totalRevenue)
                .monthlyRevenue(monthlyRevenue)
                .build();

        return ApiResponse.<AdminStatsResponse>builder().result(stats).build();
    }

    // ===== Chart Data =====

    @GetMapping("/stats/chart")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AdminChartResponse> getChartData() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter labelFmt = DateTimeFormatter.ofPattern("'Tháng' MM");

        List<AdminChartResponse.MonthlyRevenue> revenueList = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDateTime monthStart = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            BigDecimal rev = purchaseRepository.sumPricePaidByPaymentStatusSince(PaymentStatus.SUCCESS, monthStart);
            if (rev == null) rev = BigDecimal.ZERO;
            revenueList.add(AdminChartResponse.MonthlyRevenue.builder()
                    .month(monthStart.format(labelFmt))
                    .revenue(rev)
                    .build());
        }

        Map<String, Long> userCountByMonth = new LinkedHashMap<>();
        for (int i = 5; i >= 0; i--) {
            LocalDateTime monthStart = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            String key = monthStart.format(labelFmt);
            userCountByMonth.put(key, 0L);
        }
        List<User> allUsers = userRepository.findAll();
        for (User u : allUsers) {
            if (u.getCreatedAt() != null) {
                LocalDateTime created = u.getCreatedAt();
                LocalDateTime cutoff = now.minusMonths(5).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                if (!created.isBefore(cutoff)) {
                    String key = created.format(labelFmt);
                    if (userCountByMonth.containsKey(key)) {
                        userCountByMonth.put(key, userCountByMonth.get(key) + 1);
                    }
                }
            }
        }
        List<AdminChartResponse.MonthlyUser> userList = userCountByMonth.entrySet().stream()
                .map(e -> AdminChartResponse.MonthlyUser.builder()
                        .month(e.getKey())
                        .newUsers(e.getValue())
                        .build())
                .collect(Collectors.toList());

        Map<String, Long> statusCounts = new LinkedHashMap<>();
        for (ReportStatus rs : ReportStatus.values()) {
            statusCounts.put(rs.name(), reportRepository.countByStatus(rs));
        }
        List<AdminChartResponse.ReportStatusCount> reportList = statusCounts.entrySet().stream()
                .map(e -> AdminChartResponse.ReportStatusCount.builder()
                        .status(e.getKey())
                        .count(e.getValue())
                        .build())
                .collect(Collectors.toList());

        AdminChartResponse chart = AdminChartResponse.builder()
                .monthlyRevenue(revenueList)
                .monthlyUsers(userList)
                .reportStatus(reportList)
                .build();

        return ApiResponse.<AdminChartResponse>builder().result(chart).build();
    }

    // ===== Job: hide / unhide =====

    @PutMapping("/jobs/{id}/hide")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> hideJob(@PathVariable Long id) {
        com.unipart.unipart_backend.entity.Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Công việc không tồn tại"));
        job.setIsHide(true);
        jobRepository.save(job);
        sendNotification(job.getEmployerId(),
                "Tin tuyển dụng bị ẩn",
                "Tin tuyển dụng \"" + job.getTitle() + "\" của bạn đã bị admin ẩn do vi phạm quy định.");
        return ApiResponse.<String>builder().result("Đã ẩn công việc thành công").build();
    }

    @PutMapping("/jobs/{id}/unhide")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> unhideJob(@PathVariable Long id) {
        com.unipart.unipart_backend.entity.Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Công việc không tồn tại"));
        job.setIsHide(false);
        jobRepository.save(job);
        sendNotification(job.getEmployerId(),
                "Tin tuyển dụng được bỏ ẩn",
                "Tin tuyển dụng \"" + job.getTitle() + "\" của bạn đã được admin bỏ ẩn và hiển thị trở lại.");
        return ApiResponse.<String>builder().result("Đã bỏ ẩn công việc thành công").build();
    }

    // ===== Post: hide / unhide =====

    @PutMapping("/posts/{id}/hide")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> hidePost(@PathVariable Long id) {
        com.unipart.unipart_backend.entity.Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại"));
        post.setIsHide(true);
        postRepository.save(post);
        sendNotification(post.getUserId(),
                "Bài viết của bạn bị ẩn",
                "Một bài viết của bạn đã bị admin ẩn do vi phạm quy định cộng đồng.");
        return ApiResponse.<String>builder().result("Đã ẩn bài viết thành công").build();
    }

    @PutMapping("/posts/{id}/unhide")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> unhidePost(@PathVariable Long id) {
        com.unipart.unipart_backend.entity.Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại"));
        post.setIsHide(false);
        postRepository.save(post);
        sendNotification(post.getUserId(),
                "Bài viết được bỏ ẩn",
                "Bài viết của bạn đã được admin bỏ ẩn và hiển thị trở lại trên cộng đồng.");
        return ApiResponse.<String>builder().result("Đã bỏ ẩn bài viết thành công").build();
    }

    // ===== Helper =====

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
            System.err.println("[AdminController] Failed to send notification: " + e.getMessage());
        }
    }
}
