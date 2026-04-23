package com.unipart.unipart_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentRegistrationRequest {

    private String username;
    private String email;
    private String password;
    private String fullName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String gender;

    private String university;
    private String major;
    private String address;
    private Double latitude;
    private Double longitude;
}