package com.unipart.unipart_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployerResponse {

    // Thông tin cơ bản từ thực thể User
    private String id;
    private String username;
    private String email;
    private String fullName;
    private String avatar;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String gender;
    private Boolean isBlocked;
    private Boolean isActived;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Thông tin chi tiết từ thực thể Employer
    private String companyName;
    private String companyAddress;
    private Double latitude;
    private Double longitude;
    private String description;
    private Double rating;
}