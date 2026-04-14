package com.unipart.unipart_backend.dto.request;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentUpdateRequest {

    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String gender;
    private LocalDate dateOfBirth;

    private String university;
    private String major;
    private String address;
    private Double latitude;
    private Double longitude;
}