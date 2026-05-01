package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.Review;
import com.unipart.unipart_backend.enums.ReviewType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByStudentIdAndJobIdAndReviewType(
            String studentId, Long jobId, ReviewType reviewType);

    List<Review> findByEmployerIdAndReviewType(
            String employerId, ReviewType reviewType);

    List<Review> findByStudentIdAndReviewType(
            String studentId, ReviewType reviewType);
}