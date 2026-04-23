package com.unipart.unipart_backend.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
