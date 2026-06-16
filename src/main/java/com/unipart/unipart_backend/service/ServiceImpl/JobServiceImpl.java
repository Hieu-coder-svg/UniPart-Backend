package com.unipart.unipart_backend.service.ServiceImpl;
import com.unipart.unipart_backend.entity.Application;
import com.unipart.unipart_backend.entity.Student;
import com.unipart.unipart_backend.repository.ApplicationJobRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import com.unipart.unipart_backend.dto.request.JobCreationRequest;
import com.unipart.unipart_backend.dto.request.JobUpdateRequest;
import com.unipart.unipart_backend.dto.request.JobFilterRequest;
import com.unipart.unipart_backend.enums.JobType;
import com.unipart.unipart_backend.dto.response.JobResponse;
import com.unipart.unipart_backend.entity.Employer;
import com.unipart.unipart_backend.entity.Job;
import com.unipart.unipart_backend.entity.JobTimeSlot;
import com.unipart.unipart_backend.exception.AppException;
import com.unipart.unipart_backend.exception.ErrorCode;
import com.unipart.unipart_backend.mapper.JobMapper;
import com.unipart.unipart_backend.repository.EmployerRepository;
import com.unipart.unipart_backend.repository.JobRepository;
import com.unipart.unipart_backend.repository.JobTimeSlotRepository;
import com.unipart.unipart_backend.repository.UserRepository;
import com.unipart.unipart_backend.repository.EmployerPostQuotaRepository;
import com.unipart.unipart_backend.entity.EmployerPostQuota;
import com.unipart.unipart_backend.service.JobService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
@Service
@Transactional
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JobTimeSlotRepository jobTimeSlotRepository;
    private final EmployerRepository employerRepository;
    private final JobMapper jobMapper;
    private final UserRepository userRepository;
    private final ApplicationJobRepository applicationJobRepository;
    private final EmployerPostQuotaRepository employerPostQuotaRepository;

    public JobResponse createJob(JobCreationRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Employer employer = employerRepository.findByUserUsername(username);

        String quotaType = Boolean.TRUE.equals(request.getUrgent()) ? "URGENT" : "NORMAL";
        List<EmployerPostQuota> quotas = employerPostQuotaRepository.findAllByEmployerId(employer.getId());
        EmployerPostQuota validQuota = quotas.stream()
                .filter(q -> {
                    if (!quotaType.equals(q.getQuotaType()) || q.getRemainingPosts() <= 0) return false;
                    if (q.getExpiresAt() != null && q.getExpiresAt().isBefore(LocalDateTime.now())) return false;
                    if (q.getMaxPostsPerDay() != null) {
                        java.time.LocalDate today = java.time.LocalDate.now();
                        int used = (q.getLastResetDate() != null && q.getLastResetDate().equals(today) && q.getUsedPostsToday() != null) ? q.getUsedPostsToday() : 0;
                        if (used >= q.getMaxPostsPerDay()) return false;
                    }
                    return true;
                })
                .sorted((q1, q2) -> {
                    boolean q1Monthly = "MONTHLY".equals(q1.getType()) || (q1.getType() == null && q1.getExpiresAt() != null);
                    boolean q2Monthly = "MONTHLY".equals(q2.getType()) || (q2.getType() == null && q2.getExpiresAt() != null);
                    if (q1Monthly && !q2Monthly) return -1;
                    if (!q1Monthly && q2Monthly) return 1;
                    return 0;
                })
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.INSUFFICIENT_QUOTA));

        validQuota.setRemainingPosts(validQuota.getRemainingPosts() - 1);
        if ("MONTHLY".equals(validQuota.getType()) || (validQuota.getType() == null && validQuota.getExpiresAt() != null)) {
            java.time.LocalDate today = java.time.LocalDate.now();
            if (validQuota.getLastResetDate() == null || !validQuota.getLastResetDate().equals(today)) {
                 validQuota.setUsedPostsToday(1);
                 validQuota.setLastResetDate(today);
            } else {
                 validQuota.setUsedPostsToday((validQuota.getUsedPostsToday() != null ? validQuota.getUsedPostsToday() : 0) + 1);
            }
        }
        employerPostQuotaRepository.save(validQuota);

        Job job = jobMapper.toJobEntity(request);
        job.setEmployerId(employer.getId());
        job.setCreatedAt(LocalDateTime.now());
        job.setIsHide(false);
        final Job savedJob = jobRepository.save(job);

        if (request.getTimeSlots() != null && !request.getTimeSlots().isEmpty()) {
            List<JobTimeSlot> timeSlots = request.getTimeSlots().stream()
                    .map(slotRequest -> {
                        JobTimeSlot slot = jobMapper.toTimeSlotEntity(slotRequest);
                        slot.setJobId(savedJob.getId());
                        slot.setJob(savedJob);
                        return slot;
                    })
                    .collect(Collectors.toList());
            jobTimeSlotRepository.saveAll(timeSlots);
            savedJob.setJobTimeSlots(new java.util.HashSet<>(timeSlots));
        }

        return jobMapper.toJobResponse(savedJob);
    }

    public JobResponse updateJob(Long jobId, JobUpdateRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công việc với ID: " + jobId));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!job.getEmployer().getUser().getUsername().equals(username)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa công việc này");
        }

        // Kiểm tra quota nếu đổi từ tin thường thành tin tuyển gấp
        if (!Boolean.TRUE.equals(job.getUrgent()) && Boolean.TRUE.equals(request.getUrgent())) {
            List<EmployerPostQuota> quotas = employerPostQuotaRepository.findAllByEmployerId(job.getEmployerId());
            EmployerPostQuota validQuota = quotas.stream()
                    .filter(q -> {
                        if (!"URGENT".equals(q.getQuotaType()) || q.getRemainingPosts() <= 0) return false;
                        if (q.getExpiresAt() != null && q.getExpiresAt().isBefore(LocalDateTime.now())) return false;
                        if (q.getMaxPostsPerDay() != null) {
                            java.time.LocalDate today = java.time.LocalDate.now();
                            int used = (q.getLastResetDate() != null && q.getLastResetDate().equals(today) && q.getUsedPostsToday() != null) ? q.getUsedPostsToday() : 0;
                            if (used >= q.getMaxPostsPerDay()) return false;
                        }
                        return true;
                    })
                    .sorted((q1, q2) -> {
                        boolean q1Monthly = "MONTHLY".equals(q1.getType()) || (q1.getType() == null && q1.getExpiresAt() != null);
                        boolean q2Monthly = "MONTHLY".equals(q2.getType()) || (q2.getType() == null && q2.getExpiresAt() != null);
                        if (q1Monthly && !q2Monthly) return -1;
                        if (!q1Monthly && q2Monthly) return 1;
                        return 0;
                    })
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.INSUFFICIENT_QUOTA));

            validQuota.setRemainingPosts(validQuota.getRemainingPosts() - 1);
            if ("MONTHLY".equals(validQuota.getType()) || (validQuota.getType() == null && validQuota.getExpiresAt() != null)) {
                java.time.LocalDate today = java.time.LocalDate.now();
                if (validQuota.getLastResetDate() == null || !validQuota.getLastResetDate().equals(today)) {
                     validQuota.setUsedPostsToday(1);
                     validQuota.setLastResetDate(today);
                } else {
                     validQuota.setUsedPostsToday((validQuota.getUsedPostsToday() != null ? validQuota.getUsedPostsToday() : 0) + 1);
                }
            }
            employerPostQuotaRepository.save(validQuota);
        }

        jobMapper.updateJobFromRequest(request, job);

        if (request.getTimeSlots() != null) {
            // Fix: Don't re-assign the collection because of orphanRemoval = true
            job.getJobTimeSlots().clear();
            
            List<JobTimeSlot> newTimeSlots = request.getTimeSlots().stream()
                    .map(slotRequest -> {
                        JobTimeSlot slot = jobMapper.toTimeSlotEntity(slotRequest);
                        slot.setJobId(job.getId());
                        slot.setJob(job);
                        return slot;
                    })
                    .collect(Collectors.toList());
            
            job.getJobTimeSlots().addAll(newTimeSlots);
        }

        return jobMapper.toJobResponse(jobRepository.save(job));
    }

    public JobResponse getJobDetail(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công việc"));
        return jobMapper.toJobResponse(job);
    }

    public void incrementViewCount(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công việc"));
        if (job.getViewCount() == null) {
            job.setViewCount(1);
        } else {
            job.setViewCount(job.getViewCount() + 1);
        }
        jobRepository.save(job);
    }
    public List<JobResponse> getMyJobPost() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(username).orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXIST));

        List<Job> jobs = jobRepository.findAllByEmployerId(user.getId());
        jobs.sort((j1, j2) -> j2.getCreatedAt().compareTo(j1.getCreatedAt()));
        return jobMapper.toJobResponseList(jobs);
    }

    public Page<JobResponse> getAllJobs(JobFilterRequest request) {
        Sort sort = Sort.by(Sort.Direction.DESC, "urgent")
                .and(Sort.by(request.getSortDirection(), request.getSortBy()));
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Specification<Job> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getEmployerId() != null && !request.getEmployerId().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("employerId"), request.getEmployerId()));
            }
            if (request.getTitle() != null && !request.getTitle().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + request.getTitle().toLowerCase() + "%"));
            }
            if (request.getWorkingShift() != null && !request.getWorkingShift().isEmpty()) {
                CriteriaBuilder.In<String> inClause = criteriaBuilder.in(root.get("workingShift"));
                for (String shift : request.getWorkingShift()) {
                    inClause.value(shift);
                }
                predicates.add(inClause);
            }

            if (request.getJobType() != null && !request.getJobType().isEmpty()) {
                CriteriaBuilder.In<JobType> inClause = criteriaBuilder.in(root.get("jobType"));
                for (JobType type : request.getJobType()) {
                    inClause.value(type);
                }
                predicates.add(inClause);
            }

            if (request.getUrgent() != null) {
                predicates.add(criteriaBuilder.equal(root.get("urgent"), request.getUrgent()));
            }
            if (request.getAddress() != null && !request.getAddress().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), "%" + request.getAddress().toLowerCase() + "%"));
            }
            if (request.getMinSalary() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("salary"), request.getMinSalary()));
            }
            if (request.getMaxSalary() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("salary"), request.getMaxSalary()));
            }
            if (request.getCreatedAfter() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), request.getCreatedAfter()));
            }
            if (request.getExpiresBefore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("expiredAt"), request.getExpiresBefore()));
            }
            if (request.getIsHide() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isHide"), request.getIsHide()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Job> jobs = jobRepository.findAll(specification, pageable);
        return jobs.map(jobMapper::toJobResponse);
    }
    public List<JobResponse> getStudentJobHistory(String studentId) {
        List<Application> applications = applicationJobRepository.findByStudentId(studentId);
        return applications.stream()
                .map(application -> {
                    Job job = application.getJob();
                    JobResponse jobResponse = jobMapper.toJobResponse(job);
                    jobResponse.setStatus(application.getStatus().name());
                    jobResponse.setApplicationId(application.getId());
                    return jobResponse;
                })
                .collect(Collectors.toList());
    }
}
