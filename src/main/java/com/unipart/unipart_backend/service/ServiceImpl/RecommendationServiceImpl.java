package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.response.JobRecommendationResponse;
import com.unipart.unipart_backend.entity.Job;
import com.unipart.unipart_backend.entity.Student;
import com.unipart.unipart_backend.mapper.JobMapper;
import com.unipart.unipart_backend.repository.JobRepository;
import com.unipart.unipart_backend.repository.StudentRepository;
import com.unipart.unipart_backend.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final JobRepository jobRepository;
    private final StudentRepository studentRepository;
    private final JobMapper jobMapper;

    @Override
    public List<JobRecommendationResponse> getRecommendedJobsForStudent(String studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);

        // Fetch active jobs
        List<Job> activeJobs = jobRepository.findByIsHideFalseAndExpiredAtAfter(LocalDateTime.now());

        if (student == null || student.getLatitude() == null || student.getLongitude() == null) {
            // If student has no location setup, return randomly or based on urgency
            return activeJobs.stream()
                    .map(job -> new JobRecommendationResponse(jobMapper.toJobResponse(job), calculateUrgentScore(job)))
                    .sorted((j1, j2) -> Double.compare(j2.getMatchScore(), j1.getMatchScore()))
                    .limit(20)
                    .collect(Collectors.toList());
        }

        List<JobRecommendationResponse> recommendedJobs = new ArrayList<>();

        for (Job job : activeJobs) {
            double totalScore = 0.0;

            // 1. Location Score
            if (job.getLocationLatitude() != null && job.getLocationLongitude() != null) {
                double distance = calculateDistance(
                        student.getLatitude(), student.getLongitude(),
                        job.getLocationLatitude().doubleValue(), job.getLocationLongitude().doubleValue()
                );
                totalScore += calculateDistanceScore(distance);
            }

            // 2. Urgent Score
            totalScore += calculateUrgentScore(job);

            recommendedJobs.add(new JobRecommendationResponse(jobMapper.toJobResponse(job), totalScore));
        }

        // Sort by highest score first
        recommendedJobs.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));

        // Return top 20 recommendations
        return recommendedJobs.stream().limit(20).collect(Collectors.toList());
    }

    private double calculateDistanceScore(double distanceInKm) {
        if (distanceInKm < 3.0) return 30.0;
        if (distanceInKm < 7.0) return 20.0;
        if (distanceInKm < 12.0) return 10.0;
        return 0.0;
    }

    private double calculateUrgentScore(Job job) {
        if (job.getUrgent() != null && job.getUrgent()) {
            return 15.0;
        }
        return 0.0;
    }

    // Haversine formula
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        int R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2 - lat1);
        double dLon = deg2rad(lon2 - lon1);
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }

    private double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }
}
