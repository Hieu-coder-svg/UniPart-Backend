package com.unipart.unipart_backend.dto.request;

import com.unipart.unipart_backend.entity.JobTimeSlot;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JobCreationRequest {

    private String employerId;
    private String title;
    private String image;   // URL ảnh job
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
