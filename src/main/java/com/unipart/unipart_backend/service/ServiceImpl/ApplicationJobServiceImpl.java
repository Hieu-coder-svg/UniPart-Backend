package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.request.ApplyJobRequest;
import com.unipart.unipart_backend.dto.request.ApplyJobUpdateRequest;
import com.unipart.unipart_backend.dto.request.NotificationCreationRequest;
import com.unipart.unipart_backend.dto.response.ApplicationResponse;
import com.unipart.unipart_backend.entity.Application;
import com.unipart.unipart_backend.entity.Employer;
import com.unipart.unipart_backend.entity.Job;
import com.unipart.unipart_backend.entity.Notification;
import com.unipart.unipart_backend.entity.Student;
import com.unipart.unipart_backend.entity.User;
import com.unipart.unipart_backend.enums.ApplicationStatus;
import com.unipart.unipart_backend.exception.AppException;
import com.unipart.unipart_backend.exception.ErrorCode;
import com.unipart.unipart_backend.mapper.ApplicationMapper;
import com.unipart.unipart_backend.mapper.NotificationMapper;
import com.unipart.unipart_backend.repository.ApplicationJobRepository;
import com.unipart.unipart_backend.repository.ApplicationRepository;
import com.unipart.unipart_backend.repository.EmployerRepository;
import com.unipart.unipart_backend.repository.JobRepository;
import com.unipart.unipart_backend.repository.StudentRepository;
import com.unipart.unipart_backend.repository.UserRepository;
import com.unipart.unipart_backend.service.ApplicationJobService;
import com.unipart.unipart_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ApplicationJobServiceImpl implements ApplicationJobService {
    private final ApplicationJobRepository applicationJobRepository;
    private final ApplicationMapper applicationMapper;
    private final StudentRepository studentRepository;
    private final EmployerRepository employerRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;
    private final ApplicationRepository applicationRepository;

    private Student getCurrentStudent() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return studentRepository.findByUsernameWithUser(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
    }


    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public ApplicationResponse applyJob(ApplyJobRequest request) {
        Student student = getCurrentStudent();
          Job job = jobRepository.findById(request.getJobId())
                .orElseThrow();
        if (applicationJobRepository.existsByStudentIdAndJobId(student.getId(), job.getId())) {
            throw new AppException(ErrorCode.INVALID_APPLICATION);
        }

        Application application = Application.builder()
                .studentId(student.getId())
                .student(student)
                .jobId(job.getId())
                .job(job)
                .status(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .build();

        Application savedApplication = applicationJobRepository.save(application);
        NotificationCreationRequest requestNotification = NotificationCreationRequest.builder()
                .userId(job.getEmployer().getUser().getId())
                .title("Ứng tuyển công việc")
                .content("Sinh viên " + student.getUser().getFullName() + " muốn làm công việc "+job.getTitle())
                .build();

        notificationService.createNotification(requestNotification);

        return applicationMapper.toResponse(savedApplication);
    }

    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public void deleteApplicationJob(Long id) {
        Application application = applicationJobRepository.findById(id)
                .orElseThrow();
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new AppException(ErrorCode.REMOVE_APPLICATION);
        }
        Student student = application.getStudent();
        Job job = jobRepository.findById(application.getJobId())
                .orElseThrow();
        NotificationCreationRequest requestNotification = NotificationCreationRequest.builder()
                .userId(job.getEmployerId())
                .title("Hủy ứng tuyển công việc")
                .content("Sinh viên " + student.getUser().getFullName() + " đã hủy công việc "+job.getTitle())
                .build();

        notificationService.createNotification(requestNotification);
        applicationJobRepository.delete(application);
    }
    public List<ApplicationResponse> getStudentApplications() {
        // 1. Lấy Student đang đăng nhập (để có ID chuẩn dạng UUID)
        Student student = getCurrentStudent();

        // 2. Tìm trong database bằng student.getId()
        List<Application> applications = applicationRepository.findByStudentId(student.getId());

        // 3. Chuyển đổi (Map) Entity sang DTO (ApplicationResponse)
        return applications.stream().map(app -> {
            return ApplicationResponse.builder()
                    .id(app.getId())
                    .jobId(app.getJob().getId())
                    .jobTitle(app.getJob().getTitle())
                    .studentId(app.getStudent().getId())
                    .status(app.getStatus().name())
                    .appliedAt(app.getAppliedAt()) // Đây chính là trường quan trọng để frontend lấy ngày!
                    .completedAt(app.getCompletedAt())
                    .build();
        }).toList();
    }
    @Override
    @PreAuthorize("hasRole('EMPLOYER')")
    public ApplicationResponse changeStatus(ApplyJobUpdateRequest request) {
        Application application = applicationJobRepository.findById(request.getApplicationId())
                .orElseThrow();
        Job job = application.getJob();
        Employer employer = job.getEmployer();
        NotificationCreationRequest requestNotification =  NotificationCreationRequest.builder()
                .userId(application.getStudentId())
                .build();
        ApplicationStatus status = ApplicationStatus.valueOf(
                request.getStatus().trim().toUpperCase()
        );
        application.setStatus(status);
        if ("COMPLETED".equals(request.getStatus())) {
            application.setCompletedAt(LocalDateTime.now());
            requestNotification.setTitle("Xác nhận hoàn thành công việc");
            requestNotification.setContent("Nhà tuyển dụng " + employer.getUser().getFullName() + " đã xác nhận hoàn thành công việc này. Bạn có thể đánh giá ngay bây giờ");
        }
        if("ACCEPTED".equals(request.getStatus())) {
            requestNotification.setTitle("Xác nhận công việc");
            requestNotification.setContent("Nhà tuyển dụng " + employer.getUser().getFullName() + " đã xác nhận bạn làm công việc này");
            application.setCompletedAt(null);
        }
        if("REJECTED".equals(request.getStatus())) {
            requestNotification.setTitle("Từ chối công việc");
            requestNotification.setContent("Nhà tuyển dụng " + employer.getUser().getFullName() + " đã từ chối bạn làm công việc này");
            application.setCompletedAt(null);
        }
        notificationService.createNotification(requestNotification);
        Application updatedApplication = applicationJobRepository.save(application);
        return applicationMapper.toResponse(updatedApplication);
    }
}
