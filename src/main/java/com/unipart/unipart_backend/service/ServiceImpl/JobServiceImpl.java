package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.request.JobCreationRequest;
import com.unipart.unipart_backend.dto.request.JobUpdateRequest;
import com.unipart.unipart_backend.dto.response.JobResponse;
import com.unipart.unipart_backend.entity.Job;
import com.unipart.unipart_backend.entity.User;
import com.unipart.unipart_backend.exception.AppException;
import com.unipart.unipart_backend.exception.ErrorCode;
import com.unipart.unipart_backend.mapper.JobMapper;
import com.unipart.unipart_backend.repository.JobRepository;
import com.unipart.unipart_backend.repository.UserRepository;
import com.unipart.unipart_backend.service.JobService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
@Service
public class JobServiceImpl implements JobService {
    JobRepository jobRepository;
    JobMapper jobMapper;
    UserRepository userRepository;
    @PreAuthorize("hasRole('EMPLOYER')")
    public JobResponse createJob(JobCreationRequest job) {
        Job j =  jobMapper.toJob(job);
        j.setIsHide(false);
        Job savedJob = jobRepository.save(j);
        return jobMapper.toJobResponse(savedJob);
    }
    @PreAuthorize("hasRole('EMPLOYER')")
    public JobResponse updateJob(long jobId ,JobUpdateRequest request) {
        Job j = jobRepository.findById(jobId).orElseThrow();
        jobMapper.updateJob(j,request);
        return jobMapper.toJobResponse(jobRepository.save(j));
    }
    @PreAuthorize("hasRole('EMPLOYER')")
    public List<JobResponse> getMyJob(Long id) {
        var context = SecurityContextHolder.getContext();
        String userName = context.getAuthentication().getName();

        User u =  userRepository.findByUsername(userName).orElseThrow(
                ()->new AppException(ErrorCode.USER_NOT_EXIST));
        List<Job> jobs =  jobRepository.findAllByEmployerId(u.getId());
        return jobMapper.toJobResponseList(jobs);
    }

    @Override
    public JobResponse getJobDetails(Long id) {
        return null;
    }

    public JobResponse getJob(Long id) {
        Job job = jobRepository.findById(id).orElseThrow();
        return jobMapper.toJobResponse(job);
    }
    public List<Job> getAll() {
        return jobRepository.findAll();
    }
}
