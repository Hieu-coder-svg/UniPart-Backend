package com.unipart.unipart_backend.dto.request;

import com.unipart.unipart_backend.entity.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
