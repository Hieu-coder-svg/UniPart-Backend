package com.unipart.unipart_backend.service.ServiceImpl;

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
        app.setCompletedAt(LocalDateTime.now());
        applicationRepository.save(app);

        //Auto-close job khi đủ vacancies
        if (acceptedCount + 1 >= job.getVacancies()) {
            job.setIsHide(true);
            jobRepository.save(job);
        }

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

        return applicationMapper.toResponse(app);
    }
}