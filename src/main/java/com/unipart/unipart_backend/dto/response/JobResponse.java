package com.unipart.unipart_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.unipart.unipart_backend.enums.JobType;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponse {
    private Long id;
    private String employerId;
    private String employerName;
    private String title;
    private String image;
    private String description;
    private String workingShift;
    private JobType jobType;
    private Integer vacancies;
    private Boolean urgent;
    private String address;
    private BigDecimal locationLatitude;
    private BigDecimal locationLongitude;
    private BigDecimal salary;
    private Boolean isHide;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private List<JobTimeSlotResponse> timeSlots;
private String status;
    private Long applicationId;
    private Integer viewCount;
}
