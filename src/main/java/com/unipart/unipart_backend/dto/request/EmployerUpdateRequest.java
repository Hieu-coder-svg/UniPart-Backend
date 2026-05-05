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
 public class EmployerUpdateRequest {
    private String fullName;
    private String email;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String gender;
    private String avatar;
    private String companyName;
    private String companyAddress;
    private Double latitude;
    private Double longitude;
    private String description;
}