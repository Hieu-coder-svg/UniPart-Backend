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
public class UserUpdateRequest {
    private String username;
    private String email;
    private String passwordHash;
    private String fullName;
    private String address;
    private String phoneNumber;
    private String gender;
    private LocalDate dateOfBirth;
    private boolean isBlocked;
    private double locationLatitude;
    private double locationLongitude;
    private LocalDateTime updatedAt;

}
