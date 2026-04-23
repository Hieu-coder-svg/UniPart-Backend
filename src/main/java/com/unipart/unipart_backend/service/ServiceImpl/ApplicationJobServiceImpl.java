package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.request.ApplyJobRequest;
import com.unipart.unipart_backend.dto.request.ApplyJobUpdateRequest;
import com.unipart.unipart_backend.dto.response.ApplicationResponse;
import com.unipart.unipart_backend.entity.Application;
import com.unipart.unipart_backend.entity.Employer;
import com.unipart.unipart_backend.entity.Job;
import com.unipart.unipart_backend.entity.Student;
import com.unipart.unipart_backend.entity.User;
import com.unipart.unipart_backend.exception.AppException;
import com.unipart.unipart_backend.mapper.ApplicationMapper;
import com.unipart.unipart_backend.repository.ApplicationJobRepository;
import com.unipart.unipart_backend.repository.EmployerRepository;
import com.unipart.unipart_backend.repository.JobRepository;
import com.unipart.unipart_backend.repository.StudentRepository;
import com.unipart.unipart_backend.repository.UserRepository;
import com.unipart.unipart_backend.service.ApplicationJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ApplicationJobServiceImpl implements ApplicationJobService {
    private final ApplicationJobRepository applicationJobRepository;
    private final ApplicationMapper applicationMapper;
    private final StudentRepository studentRepository;
    private final EmployerRepository employerRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public ApplicationResponse applyJob(ApplyJobRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User u = userRepository.findByUsername(username).orElseThrow();
        Student student = studentRepository.findById(u.getId()).orElseThrow();

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow();

        Application application = Application.builder()
                .studentId(student.getId())
                .student(student)
                .jobId(job.getId())
                .job(job)
                .status("PENDING")
                .appliedAt(LocalDateTime.now())
                .build();

        Application savedApplication = applicationJobRepository.save(application);
        return applicationMapper.toResponse(savedApplication);
    }

    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public void deleteApplicationJob(Long id) {
        Application application = applicationJobRepository.findById(id)
                .orElseThrow();
        applicationJobRepository.delete(application);
    }

    @Override
    @PreAuthorize("hasRole('EMPLOYER')")
    public ApplicationResponse changeStatus(ApplyJobUpdateRequest request) {
        Application application = applicationJobRepository.findById(request.getApplicationId())
                .orElseThrow();

        application.setStatus(request.getStatus());
        if ("COMPLETED".equals(request.getStatus())) {
            application.setCompletedAt(LocalDateTime.now());
        } else {
            application.setCompletedAt(null);
        }

        Application updatedApplication = applicationJobRepository.save(application);
        return applicationMapper.toResponse(updatedApplication);
    }
}
