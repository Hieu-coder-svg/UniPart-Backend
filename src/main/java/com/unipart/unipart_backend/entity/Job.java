package com.unipart.unipart_backend.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "employer_id", nullable = false, length = 50)
    private String employerId;   // Nếu bạn đã có entity User thì đổi thành @ManyToOne User employer

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 255)
    private String image;   // URL ảnh job

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer vacancies;

    @Column(nullable = false)
    private Boolean urgent;
    @Column(length = 255,name = "address")
    private String address;
    @Column(name = "location_latitude", precision = 10, scale = 8)
    private BigDecimal locationLatitude;

    @Column(name = "location_longitude", precision = 11, scale = 8)
    private BigDecimal locationLongitude;

    @Column(precision = 10, scale = 2)
    private BigDecimal salary;

    @Column(nullable = false)
    private boolean isHide ;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Quan hệ 1 Job - N JobTimeSlot
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<JobTimeSlot> timeSlots = new ArrayList<>();


}