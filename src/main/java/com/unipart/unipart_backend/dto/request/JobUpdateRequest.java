package com.unipart.unipart_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobUpdateRequest {
    private String title;
    private String image;
    private String description;
    private String workingShift;
    private Integer vacancies;
    private Boolean urgent;
    private String address;
    private BigDecimal locationLatitude;
    private BigDecimal locationLongitude;
    private BigDecimal salary;
    private Boolean isHide;
    private LocalDateTime expiredAt;
    private List<JobTimeSlotRequest> timeSlots;
}
