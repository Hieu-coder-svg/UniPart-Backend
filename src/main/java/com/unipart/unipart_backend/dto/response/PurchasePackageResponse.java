package com.unipart.unipart_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PurchasePackageResponse {
    private Long id;
    private String employerId;
    private Long packageId;
    private String packageName;
    private String packageType;
    private BigDecimal pricePaid;
    private String paymentStatus;       // PENDING / SUCCESS / FAILED
    private String transactionRef;
    private LocalDateTime purchasedAt;
    private LocalDateTime startDate;
    private LocalDateTime endDate;      // null nếu gói TIN
    private Integer tinsPurchased;      // null nếu gói MONTHLY
}
