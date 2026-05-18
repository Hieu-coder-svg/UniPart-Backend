package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.request.NotificationCreationRequest;
import com.unipart.unipart_backend.dto.response.AdminStatsResponse;
import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.repository.ApplicationRepository;
import com.unipart.unipart_backend.repository.JobRepository;
import com.unipart.unipart_backend.repository.PostRepository;
import com.unipart.unipart_backend.repository.UserRepository;
import com.unipart.unipart_backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private JobRepository jobRepository;
    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private NotificationService notificationService;

    // ===== Stats =====

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AdminStatsResponse> getStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsBlockedFalse();
        long totalJobs = jobRepository.count();
        long totalRequests = applicationRepository.count();

        AdminStatsResponse stats = AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalJobs(totalJobs)
                .totalRequests(totalRequests)
                .build();

        return ApiResponse.<AdminStatsResponse>builder().result(stats).build();
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
