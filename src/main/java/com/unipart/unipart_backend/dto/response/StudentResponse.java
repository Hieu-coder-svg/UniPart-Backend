package com.unipart.unipart_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    private String id;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String gender;
    private Boolean isBlocked;
    private Boolean isActived;
    private String roleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String university;
    private String major;
    private String address;
    private Double latitude;
    private Double longitude;
}
