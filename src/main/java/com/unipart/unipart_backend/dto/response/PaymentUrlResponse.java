package com.unipart.unipart_backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentUrlResponse {
    private String paymentUrl;
    private String transactionRef;
}
