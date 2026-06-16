package com.unipart.unipart_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.unipart.unipart_backend.enums.JobType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobFilterRequest {
    private String employerId;
    private String title;
    private List<String> workingShift;
    private List<JobType> jobType;
    private Boolean urgent;
    private String address;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private LocalDateTime createdAfter;
    private LocalDateTime expiresBefore;
    private Boolean isHide;

    private int page = 0;
    private int size = 10;
    private String sortBy = "createdAt";
    private Sort.Direction sortDirection = Sort.Direction.DESC;
}
