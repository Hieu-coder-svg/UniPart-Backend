package com.unipart.unipart_backend.scheduler;

import com.unipart.unipart_backend.dto.request.NotificationCreationRequest;
import com.unipart.unipart_backend.entity.Application;
import com.unipart.unipart_backend.entity.Job;
import com.unipart.unipart_backend.entity.JobTimeSlot;
import com.unipart.unipart_backend.enums.ApplicationStatus;
import com.unipart.unipart_backend.repository.ApplicationRepository;
import com.unipart.unipart_backend.repository.NotificationRepository;
import com.unipart.unipart_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobReminderScheduler {

    private final ApplicationRepository applicationRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    // Chạy ngầm định kỳ mỗi 5 phút
    @Scheduled(cron = "0 */5 * * * *")
    public void checkJobReminders() {
        log.info("[JobReminderScheduler] Bắt đầu quét kiểm tra lịch làm việc của sinh viên...");
        try {
            List<Application> acceptedApps = applicationRepository.findAllByStatusWithJobAndTimeSlots(ApplicationStatus.ACCEPTED);
            LocalDate today = LocalDate.now();
            LocalDateTime now = LocalDateTime.now();

            for (Application app : acceptedApps) {
                Job job = app.getJob();
                if (job == null || job.getJobTimeSlots() == null) {
                    continue;
                }

                for (JobTimeSlot slot : job.getJobTimeSlots()) {
                    if (slot.getWorkDate().equals(today)) {
                        LocalTime startTime = slot.getStartTime();
                        LocalDateTime startDateTime = LocalDateTime.of(today, startTime);

                        // Kiểm tra nếu ca làm việc bắt đầu trong vòng 30 phút tới và chưa bắt đầu
                        if (startDateTime.isAfter(now) && startDateTime.isBefore(now.plusMinutes(30))) {
                            String title = "Sắp đến giờ đi làm: " + job.getTitle();
                            LocalDateTime startOfDay = today.atStartOfDay();

                            // Kiểm tra xem đã gửi thông báo nhắc nhở này hôm nay chưa để tránh spam
                            boolean alreadyNotified = notificationRepository.existsByUserIdAndTitleAndCreatedAtAfter(
                                    app.getStudentId(), title, startOfDay);

                            if (!alreadyNotified) {
                                String timeStr = startTime.toString();
                                String content = "Công việc '" + job.getTitle() + "' của bạn sẽ bắt đầu lúc " 
                                        + timeStr + " hôm nay. Hãy chuẩn bị đi làm nhé!";

                                log.info("[JobReminderScheduler] Đang gửi thông báo nhắc nhở đi làm cho Student ID: {}, Job ID: {}, Ca bắt đầu: {}",
                                        app.getStudentId(), job.getId(), startTime);

                                NotificationCreationRequest request = NotificationCreationRequest.builder()
                                        .userId(app.getStudentId())
                                        .title(title)
                                        .content(content)
                                        .build();

                                notificationService.createNotification(request);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("[JobReminderScheduler] Gặp lỗi khi quét kiểm tra nhắc nhở: ", e);
        }
        log.info("[JobReminderScheduler] Kết thúc quét kiểm tra lịch làm việc.");
    }
}
