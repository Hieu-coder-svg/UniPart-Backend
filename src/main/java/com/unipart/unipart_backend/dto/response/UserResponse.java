package com.unipart.unipart_backend.dto.response;

import com.unipart.unipart_backend.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String id;
    private String username;
    private String email;
    private String fullName;
    private String address;
    private String phoneNumber;
    private Gender gender;
    private Boolean isBlocked;
    private String roleName;
    private Integer availableHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}