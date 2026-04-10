package com.unipart.unipart_backend.dto.request;

import com.unipart.unipart_backend.entity.JobTimeSlot;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JobUpdateRequest {
    private String title;
    private String image;
    private String description;
    private Integer vacancies ;
    private Boolean urgent;
    private String address;
    private BigDecimal locationLatitude;
    private BigDecimal locationLongitude;
    private BigDecimal salary;
    private LocalDateTime createdAt;
    private List<JobTimeSlot> timeSlots = new ArrayList<>();
}
