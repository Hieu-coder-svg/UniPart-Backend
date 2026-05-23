package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.request.NotificationCreationRequest;
import com.unipart.unipart_backend.dto.response.ApplicationResponse;
import com.unipart.unipart_backend.entity.Application;
import com.unipart.unipart_backend.entity.Job;
import com.unipart.unipart_backend.enums.ApplicationStatus;
import com.unipart.unipart_backend.exception.AppException;
import com.unipart.unipart_backend.exception.ErrorCode;
import com.unipart.unipart_backend.mapper.ApplicationMapper;
import com.unipart.unipart_backend.repository.ApplicationRepository;
import com.unipart.unipart_backend.repository.JobRepository;
import com.unipart.unipart_backend.service.ApplicationService;
import com.unipart.unipart_backend.service.EmailService;
import com.unipart.unipart_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final ApplicationMapper applicationMapper;
    private final EmailService emailService;
    private final NotificationService notificationService;

    // ===== Helper =====

    /**
     * Lấy userId (UUID) từ JWT claim "userId".
     * JWT sub = username, nhưng employerId trong DB là user.id (UUID),
     * nên phải đọc claim "userId" thay vì getName().
     */
    private String getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("userId");
        }
        // fallback nếu chạy test context không có JWT
        return authentication.getName();
    }

    private ApplicationResponse toDTO(Application a) {
        var student = a.getStudent();
        var user = student.getUser();
        return ApplicationResponse.builder()
                .id(a.getId())
                .jobId(a.getJob().getId())
                .jobTitle(a.getJob().getTitle())
                .studentId(student.getId())
                .studentName(user.getFullName())
                .studentEmail(user.getEmail())
                .studentPhone(user.getPhoneNumber())
                .studentUniversity(student.getUniversity())
                .studentMajor(student.getMajor())
                .status(a.getStatus().name())
                .appliedAt(a.getAppliedAt())
                .completedAt(a.getCompletedAt())
                .build();
    }

    // ===== GET ALL =====
    @Override
    public List<ApplicationResponse> getEmployerApplications() {
        String employerId = getCurrentUserId();

        return applicationRepository
                .findAllByEmployerIdWithDetails(employerId)
                .stream()
                .map(applicationMapper::toResponse)
                .toList();
    }

    // ===== ACCEPT =====
    @Override
    @Transactional
    public ApplicationResponse acceptApplication(Long id) {

        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        String currentUserId = getCurrentUserId();

        // Kiểm tra quyền sở hữu
        if (!app.getJob().getEmployerId().equals(currentUserId)) {
            throw new AppException(ErrorCode.APPLICATION_FORBIDDEN);
        }

        //Đã xử lý rồi
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new AppException(ErrorCode.APPLICATION_ALREADY_PROCESSED);
        }

        Job job = app.getJob();

        //Kiểm tra số lượng slot còn lại
        long acceptedCount = applicationRepository
                .countByJobIdAndStatus(job.getId(), ApplicationStatus.ACCEPTED);

        if (acceptedCount >= job.getVacancies()) {
            throw new AppException(ErrorCode.JOB_IS_FULL);
        }

        //Accept
        app.setStatus(ApplicationStatus.ACCEPTED);
        // completedAt chỉ set khi COMPLETED, không phải ACCEPTED
        applicationRepository.save(app);

        // Giảm vacancies đi 1
        job.setVacancies(job.getVacancies() - 1);

        //Auto-close job khi đủ vacancies
        if (job.getVacancies() <= 0) {
            job.setIsHide(true);
        }
        jobRepository.save(job);

        String companyName = job.getEmployer().getCompanyName() != null ? job.getEmployer().getCompanyName() : job.getEmployer().getUser().getFullName();
        String studentEmail = app.getStudent().getUser().getEmail();
        String studentName = app.getStudent().getUser().getFullName();
        
        emailService.sendApplicationAcceptedEmail(studentEmail, studentName, job.getTitle(), companyName);
        
        notificationService.createNotification(NotificationCreationRequest.builder()
                .userId(app.getStudent().getId())
                .title("Đơn ứng tuyển được chấp nhận 🎉")
                .content("Chúc mừng! Đơn ứng tuyển cho vị trí " + job.getTitle() + " tại " + companyName + " đã được chấp nhận. Vui lòng chờ nhà tuyển dụng liên hệ hoặc kiểm tra email của bạn.")
                .build());

        return applicationMapper.toResponse(app);
    }

    // ===== REJECT =====
    @Override
    @Transactional
    public ApplicationResponse rejectApplication(Long id) {

        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        String currentUserId = getCurrentUserId();

        //Kiểm tra quyền sở hữu
        if (!app.getJob().getEmployerId().equals(currentUserId)) {
            throw new AppException(ErrorCode.APPLICATION_FORBIDDEN);
        }

        //Đã xử lý rồi
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new AppException(ErrorCode.APPLICATION_ALREADY_PROCESSED);
        }

        app.setStatus(ApplicationStatus.REJECTED);
        app.setCompletedAt(LocalDateTime.now());
        applicationRepository.save(app);

        // Tăng vacancies lại 1 khi reject
        Job job = app.getJob();
        job.setVacancies(job.getVacancies() + 1);
        jobRepository.save(job);

        String companyName = job.getEmployer().getCompanyName() != null ? job.getEmployer().getCompanyName() : job.getEmployer().getUser().getFullName();
        String studentEmail = app.getStudent().getUser().getEmail();
        String studentName = app.getStudent().getUser().getFullName();
        
        emailService.sendApplicationRejectedEmail(studentEmail, studentName, job.getTitle(), companyName);
        
        notificationService.createNotification(NotificationCreationRequest.builder()
                .userId(app.getStudent().getId())
                .title("Kết quả ứng tuyển 📄")
                .content("Nhà tuyển dụng " + companyName + " đã từ chối đơn ứng tuyển của bạn cho vị trí " + job.getTitle() + ". Đừng nản lòng, hãy tiếp tục tìm kiếm cơ hội khác nhé!")
                .build());

        return applicationMapper.toResponse(app);
    }

    // ===== COMPLETE =====
    @Override
    @Transactional
    public ApplicationResponse completeApplication(Long id) {

        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        String currentUserId = getCurrentUserId();

        // Kiểm tra quyền sở hữu
        if (!app.getJob().getEmployerId().equals(currentUserId)) {
            throw new AppException(ErrorCode.APPLICATION_FORBIDDEN);
        }

        // Chỉ cho phép chuyển từ ACCEPTED sang COMPLETED
        if (app.getStatus() != ApplicationStatus.ACCEPTED) {
            throw new AppException(ErrorCode.APPLICATION_CANNOT_COMPLETE);
        }

        app.setStatus(ApplicationStatus.COMPLETED);
        app.setCompletedAt(LocalDateTime.now());
        applicationRepository.save(app);

        Job job = app.getJob();
        String companyName = job.getEmployer().getCompanyName() != null ? job.getEmployer().getCompanyName() : job.getEmployer().getUser().getFullName();
        String studentEmail = app.getStudent().getUser().getEmail();
        String studentName = app.getStudent().getUser().getFullName();
        
        emailService.sendApplicationCompletedEmail(studentEmail, studentName, job.getTitle(), companyName);
        
        notificationService.createNotification(NotificationCreationRequest.builder()
                .userId(app.getStudent().getId())
                .title("Công việc đã hoàn thành ⭐")
                .content("Nhà tuyển dụng " + companyName + " đã xác nhận bạn hoàn thành công việc cho vị trí " + job.getTitle() + ". Hãy để lại đánh giá cho họ nhé!")
                .build());

        return applicationMapper.toResponse(app);
    }
}