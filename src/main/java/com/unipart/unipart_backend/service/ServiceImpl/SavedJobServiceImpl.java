package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.request.SavedJobRequest;
import com.unipart.unipart_backend.dto.response.SavedJobResponse;
import com.unipart.unipart_backend.entity.SavedJob;
import com.unipart.unipart_backend.entity.Student;
import com.unipart.unipart_backend.entity.User;
import com.unipart.unipart_backend.exception.AppException;
import com.unipart.unipart_backend.exception.ErrorCode;
import com.unipart.unipart_backend.mapper.SavedJobMapper;
import com.unipart.unipart_backend.repository.SavedJobRepository;
import com.unipart.unipart_backend.repository.UserRepository;
import com.unipart.unipart_backend.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedJobServiceImpl implements SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final SavedJobMapper savedJobMapper;
    private final UserRepository userRepository;
    private User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXIST));
    }

    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public SavedJobResponse saveJob(SavedJobRequest request) {
        User u = getCurrentUser();
        Optional<SavedJob> existingSavedJob = savedJobRepository.findByStudentIdAndJobId(u.getId(), request.getJobId());
        if (existingSavedJob.isPresent()) {
            // Job already saved, maybe throw an exception or return the existing one
            return savedJobMapper.toDto(existingSavedJob.get());
        }

        SavedJob savedJob = savedJobMapper.toEntity(request);
        savedJob.setSavedAt(LocalDateTime.now());
        SavedJob savedJobEntity = savedJobRepository.save(savedJob);
        return savedJobMapper.toDto(savedJobEntity);
    }

    @Override
    public void unsaveJob(String studentId, Long jobId) {
        Optional<SavedJob> savedJob = savedJobRepository.findByStudentIdAndJobId(studentId, jobId);
        savedJob.ifPresent(savedJobRepository::delete);
    }

    @Override
    public List<SavedJobResponse> getMySavedJobsByStudentId() {
        User u =getCurrentUser();
        List<SavedJob> savedJobs = savedJobRepository.findByStudentId(u.getId());
        return savedJobs.stream()
                .map(savedJobMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isJobSaved(Long jobId) {
        User u = getCurrentUser();
        return savedJobRepository.findByStudentIdAndJobId(u.getId(), jobId).isPresent();
    }
}