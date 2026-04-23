package com.unipart.unipart_backend.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PackageRequest {
    String name;           // Tên gói
    String packageType;    // "MONTHLY" hoặc "ONE_TIME"
    BigDecimal price;      // Giá
    String description;    // Mô tả

    // Chỉ dùng khi MONTHLY
    Integer durationDays;       // Thời hạn (ngày)
    Integer normalTinsLimit;    // Số tin thường / tháng
    Integer maxNormalTinsPerDay;// Số tin thường tối đa / ngày
    Integer urgentTinsLimit;    // Số tin gấp / tháng

    // Chỉ dùng khi ONE_TIME
    String tinType;        // Loại tin
    Integer tinQuantity;   // Số lượng tin
}
