package com.unipart.unipart_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentResponse {

    // ===== USER =====
    Long id;
    String username;
    String email;
    String fullName;
    String phoneNumber;
    String gender;
    LocalDate dateOfBirth;

    // ===== STUDENT =====
    String university;
    String major;
    String address;
    Double latitude;
    Double longitude;
    Double rating;
}