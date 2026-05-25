package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.response.JobRecommendationResponse;
import com.unipart.unipart_backend.entity.Application;
import com.unipart.unipart_backend.entity.Job;
import com.unipart.unipart_backend.entity.JobTimeSlot;
import com.unipart.unipart_backend.entity.Student;
import com.unipart.unipart_backend.entity.StudentSchedule;
import com.unipart.unipart_backend.entity.TimeSlot;
import com.unipart.unipart_backend.mapper.JobMapper;
import com.unipart.unipart_backend.repository.ApplicationRepository;
import com.unipart.unipart_backend.repository.JobRepository;
import com.unipart.unipart_backend.repository.StudentRepository;
import com.unipart.unipart_backend.repository.StudentScheduleRepository;
import com.unipart.unipart_backend.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final JobRepository jobRepository;
    private final StudentRepository studentRepository;
    private final JobMapper jobMapper;
    private final ApplicationRepository applicationRepository;
    private final StudentScheduleRepository studentScheduleRepository;

    @Override
    public List<JobRecommendationResponse> getRecommendedJobsForStudent(String studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);

        // Fetch active jobs
        List<Job> activeJobs = jobRepository.findByIsHideFalseAndExpiredAtAfter(LocalDateTime.now());

        if (student == null) {
            return activeJobs.stream()
                    .map(job -> new JobRecommendationResponse(jobMapper.toJobResponse(job), calculateUrgentScore(job)))
                    .sorted((j1, j2) -> Double.compare(j2.getMatchScore(), j1.getMatchScore()))
                    .limit(20)
                    .collect(Collectors.toList());
        }

        // 1. Exclusion Rule: Remove already applied jobs
        List<Application> applications = applicationRepository.findByStudentId(studentId);
        Set<Long> appliedJobIds = applications.stream()
                .map(app -> app.getJob().getId())
                .collect(Collectors.toSet());

        activeJobs = activeJobs.stream()
                .filter(job -> !appliedJobIds.contains(job.getId()))
                .collect(Collectors.toList());

        // Get student schedule
        List<StudentSchedule> studentSchedules = studentScheduleRepository.findByUserId(studentId);

        List<JobRecommendationResponse> recommendedJobs = new ArrayList<>();

        for (Job job : activeJobs) {
            // 2. Schedule Matching
            if (isScheduleConflict(job, studentSchedules)) {
                continue; // Skip jobs with conflicting schedules
            }

            double totalScore = 0.0;

            // Schedule Match Bonus (if no conflict and job has schedules)
            if (!studentSchedules.isEmpty() && job.getJobTimeSlots() != null && !job.getJobTimeSlots().isEmpty()) {
                totalScore += 25.0; // Bonus for compatible schedule
            }

            // Location Score
            if (student.getLatitude() != null && student.getLongitude() != null &&
                job.getLocationLatitude() != null && job.getLocationLongitude() != null) {
                double distance = calculateDistance(
                        student.getLatitude(), student.getLongitude(),
                        job.getLocationLatitude().doubleValue(), job.getLocationLongitude().doubleValue()
                );
                totalScore += calculateDistanceScore(distance);
            }

            // Urgent Score
            totalScore += calculateUrgentScore(job);

            // 3. Profile Matching (Major & Skills)
            totalScore += calculateProfileMatchScore(job, student);

            // 4. Recency Bonus
            totalScore += calculateRecencyBonus(job);

            // 5. Employer Rating
            if (job.getEmployer() != null && job.getEmployer().getRating() != null) {
                totalScore += job.getEmployer().getRating() * 2; // e.g., 5 stars -> 10 pts
            }

            recommendedJobs.add(new JobRecommendationResponse(jobMapper.toJobResponse(job), totalScore));
        }

        // Sort by highest score first
        recommendedJobs.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));

        // Return top 20 recommendations
        return recommendedJobs.stream().limit(20).collect(Collectors.toList());
    }

    private boolean isScheduleConflict(Job job, List<StudentSchedule> studentSchedules) {
        if (job.getJobTimeSlots() == null || job.getJobTimeSlots().isEmpty() || studentSchedules.isEmpty()) {
            return false;
        }

        for (JobTimeSlot jobSlot : job.getJobTimeSlots()) {
            String dayOfWeekStr = getDayOfWeekStr(jobSlot.getWorkDate().getDayOfWeek());
            
            for (StudentSchedule schedule : studentSchedules) {
                if (schedule.getDayOfWeek().equals(dayOfWeekStr)) {
                    for (TimeSlot busySlot : schedule.getBusyTimeSlots()) {
                        // Check overlap
                        if (busySlot.getStartTime().isBefore(jobSlot.getEndTime()) && 
                            busySlot.getEndTime().isAfter(jobSlot.getStartTime())) {
                            return true; // Conflict found
                        }
                    }
                }
            }
        }
        return false;
    }

    private String getDayOfWeekStr(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return "Thứ 2";
            case TUESDAY: return "Thứ 3";
            case WEDNESDAY: return "Thứ 4";
            case THURSDAY: return "Thứ 5";
            case FRIDAY: return "Thứ 6";
            case SATURDAY: return "Thứ 7";
            case SUNDAY: return "CN";
            default: return "";
        }
    }

    private double calculateProfileMatchScore(Job job, Student student) {
        double score = 0.0;
        String jobText = "";
        if (job.getTitle() != null) jobText += job.getTitle() + " ";
        if (job.getDescription() != null) jobText += job.getDescription();
        jobText = jobText.toLowerCase();
        
        if (student.getMajor() != null && !student.getMajor().isEmpty()) {
            if (jobText.contains(student.getMajor().toLowerCase())) {
                score += 15.0;
            }
        }
        
        if (student.getSkills() != null && !student.getSkills().isEmpty()) {
            String[] skills = student.getSkills().toLowerCase().split(",");
            for (String skill : skills) {
                if (!skill.trim().isEmpty() && jobText.contains(skill.trim())) {
                    score += 5.0;
                }
            }
            if (score > 15.0) score = 15.0; // Max 15 points for skills
        }
        
        return score;
    }

    private double calculateRecencyBonus(Job job) {
        if (job.getCreatedAt() == null) return 0.0;
        
        long hoursBetween = ChronoUnit.HOURS.between(job.getCreatedAt(), LocalDateTime.now());
        if (hoursBetween <= 24) {
            return 10.0;
        } else if (hoursBetween <= 72) {
            return 5.0;
        }
        return 0.0;
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
