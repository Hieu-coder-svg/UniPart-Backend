package com.unipart.unipart_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class PackageResponse {
    Long id;
    String name;
    String packageType;
    BigDecimal price;
    String description;
    Integer durationDays;
    Integer normalTinsLimit;
    Integer maxNormalTinsPerDay;
    Integer urgentTinsLimit;
    String tinType;
    Integer tinQuantity;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
