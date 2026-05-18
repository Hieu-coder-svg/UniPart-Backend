package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.request.ReviewRequest;
import com.unipart.unipart_backend.dto.response.ReviewResponse;
import com.unipart.unipart_backend.entity.Application;
import com.unipart.unipart_backend.entity.Employer;
import com.unipart.unipart_backend.entity.Job;
import com.unipart.unipart_backend.entity.Review;
import com.unipart.unipart_backend.entity.Student;
import com.unipart.unipart_backend.enums.ApplicationStatus;
import com.unipart.unipart_backend.enums.ReviewType;
import com.unipart.unipart_backend.exception.AppException;
import com.unipart.unipart_backend.exception.ErrorCode;
import com.unipart.unipart_backend.mapper.ReviewMapper;
import com.unipart.unipart_backend.repository.ApplicationRepository;
import com.unipart.unipart_backend.repository.EmployerRepository;
import com.unipart.unipart_backend.repository.JobRepository;
import com.unipart.unipart_backend.repository.ReviewRepository;
import com.unipart.unipart_backend.repository.StudentRepository;
import com.unipart.unipart_backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ApplicationRepository applicationRepository;
    private final EmployerRepository employerRepository;
    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;
    private final ReviewMapper reviewMapper;

    // ===== Helper =====

    /**
     * Lấy userId (UUID) từ JWT claim "userId".
     * JWT sub = username, nhưng studentId/employerId trong DB là user.id (UUID),
     * nên phải đọc claim "userId" thay vì getName().
     */
    private String getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("userId");
        }
        return authentication.getName();
    }

    // ================= STUDENT → EMPLOYER =================
    @Override
    @Transactional
    public ReviewResponse studentReviewEmployer(ReviewRequest req) {

        String studentId = getCurrentUserId();
        if (studentId == null) throw new AppException(ErrorCode.UNAUTHENTICATED);

        validateRating(req.getRating());

        // Lấy job để xác minh employerId thực sự (tránh giả mạo)
        Job job = jobRepository.findById(req.getJobId())
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));
        String employerId = job.getEmployerId();

        if (reviewRepository.existsByStudentIdAndJobIdAndReviewType(
                studentId, req.getJobId(), ReviewType.STUDENT_TO_EMPLOYER)) {
            throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        Application app = applicationRepository
                .findByStudentIdAndJobIdAndStatus(studentId, req.getJobId(), ApplicationStatus.COMPLETED)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_COMPLETED));

        Review review = Review.builder()
                .studentId(studentId)
                .employerId(employerId)
                .jobId(req.getJobId())
                .reviewType(ReviewType.STUDENT_TO_EMPLOYER)
                .rating(req.getRating())
                .comment(req.getComment())
                .build();

        reviewRepository.save(review);
        updateEmployerRating(employerId);

        return reviewMapper.toResponse(review);
    }

    // ================= EMPLOYER → STUDENT =================
    @Override
    @Transactional
    public ReviewResponse employerReviewStudent(ReviewRequest req) {

        String employerId = getCurrentUserId();
        if (employerId == null) throw new AppException(ErrorCode.UNAUTHENTICATED);

        validateRating(req.getRating());

        if (reviewRepository.existsByStudentIdAndJobIdAndReviewType(
                req.getStudentId(), req.getJobId(), ReviewType.EMPLOYER_TO_STUDENT)) {
            throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        Job job = jobRepository.findById(req.getJobId())
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        if (!job.getEmployerId().equals(employerId)) {
            throw new AppException(ErrorCode.REVIEW_JOB_FORBIDDEN);
        }

        Application app = applicationRepository
                .findByStudentIdAndJobIdAndStatus(req.getStudentId(), req.getJobId(), ApplicationStatus.COMPLETED)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_COMPLETED));

        Review review = Review.builder()
                .studentId(req.getStudentId())
                .employerId(employerId)
                .jobId(req.getJobId())
                .reviewType(ReviewType.EMPLOYER_TO_STUDENT)
                .rating(req.getRating())
                .comment(req.getComment())
                .build();

        reviewRepository.save(review);
        updateStudentRating(req.getStudentId());

        return reviewMapper.toResponse(review);
    }

    // ================= UPDATE RATING =================
    private void updateEmployerRating(String employerId) {
        List<Review> list = reviewRepository
                .findByEmployerIdAndReviewType(employerId, ReviewType.STUDENT_TO_EMPLOYER);

        double avg = list.stream().mapToInt(Review::getRating).average().orElse(0);

        Employer e = employerRepository.findById(employerId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYER_NOT_FOUND));
        e.setRating(avg);
        employerRepository.save(e);
    }

    private void updateStudentRating(String studentId) {
        List<Review> list = reviewRepository
                .findByStudentIdAndReviewType(studentId, ReviewType.EMPLOYER_TO_STUDENT);

        double avg = list.stream().mapToInt(Review::getRating).average().orElse(0);

        Student s = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
        s.setRating(avg);
        studentRepository.save(s);
    }

    private void validateRating(Integer rating) {
        if (rating == null) {
            throw new AppException(ErrorCode.REVIEW_RATING_REQUIRED);
        }
        if (rating < 1 || rating > 5) {
            throw new AppException(ErrorCode.REVIEW_RATING_INVALID);
        }
    }

    // ================= GET =================
    @Override
    public List<ReviewResponse> getEmployerReviews(String employerId) {
        return reviewMapper.toResponseList(
                reviewRepository.findByEmployerIdAndReviewType(employerId, ReviewType.STUDENT_TO_EMPLOYER));
    }

    @Override
    public List<ReviewResponse> getStudentReviews(String studentId) {
        return reviewMapper.toResponseList(
                reviewRepository.findByStudentIdAndReviewType(studentId, ReviewType.EMPLOYER_TO_STUDENT));
    }

    @Override
    public List<ReviewResponse> getReviewsWrittenByStudent(String studentId) {
        return reviewMapper.toResponseList(
                reviewRepository.findByStudentIdAndReviewType(studentId, ReviewType.STUDENT_TO_EMPLOYER));
    }

    @Override
    public List<ReviewResponse> getReviewsWrittenByEmployer(String employerId) {
        return reviewMapper.toResponseList(
                reviewRepository.findByEmployerIdAndReviewType(employerId, ReviewType.EMPLOYER_TO_STUDENT));
    }
}