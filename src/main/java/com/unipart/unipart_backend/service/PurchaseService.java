package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.dto.response.PaymentUrlResponse;


import com.unipart.unipart_backend.dto.response.PaymentUrlResponse;
import com.unipart.unipart_backend.dto.response.PurchasePackageResponse;

import java.util.List;
import java.util.Map;

public interface PurchaseService {

    /**
     * Bước 1: Employer chọn gói → tạo bản ghi PENDING + trả về URL thanh toán PayOS.
     */
    PaymentUrlResponse createPaymentUrl(Long packageId, String ipAddress);

    /**
     * Bước 2: Nhận webhook từ PayOS → verify chữ ký → cập nhật trạng thái DB.
     */
    void handlePayOSWebhook(vn.payos.model.webhooks.Webhook webhookBody);

    /**
     * Lấy lịch sử mua gói của employer đang đăng nhập.
     */
    List<PurchasePackageResponse> getMyPurchases();
}
