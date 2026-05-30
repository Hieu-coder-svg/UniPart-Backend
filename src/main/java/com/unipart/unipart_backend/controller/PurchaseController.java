package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.dto.response.PaymentUrlResponse;
import com.unipart.unipart_backend.dto.response.PurchasePackageResponse;
import com.unipart.unipart_backend.service.PurchaseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    /**
     * Employer tạo đơn thanh toán → nhận về paymentUrl để redirect sang VNPay.
     * POST /employer/packages/{packageId}/payment/create
     */
    @PostMapping("/employer/packages/{packageId}/payment/create")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ApiResponse<PaymentUrlResponse> createPayment(
            @PathVariable Long packageId,
            HttpServletRequest request) {

        String ipAddress = getClientIp(request);
        return ApiResponse.<PaymentUrlResponse>builder()
                .result(purchaseService.createPaymentUrl(packageId, ipAddress))
                .build();
    }

    /**
     * PayOS gọi webhook sau khi user thanh toán thành công.
     * POST /home/payment/payos-webhook
     * (Public — không cần JWT vì PayOS gọi thẳng)
     */
    @PostMapping("/home/payment/payos-webhook")
    public ApiResponse<String> payosWebhook(@org.springframework.web.bind.annotation.RequestBody vn.payos.model.webhooks.Webhook webhookBody) {
        purchaseService.handlePayOSWebhook(webhookBody);
        return ApiResponse.<String>builder()
                .result("success")
                .build();
    }

    /**
     * Employer xem lịch sử các gói đã mua.
     * GET /employer/purchases
     */
    @GetMapping("/employer/purchases")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ApiResponse<List<PurchasePackageResponse>> getMyPurchases() {
        return ApiResponse.<List<PurchasePackageResponse>>builder()
                .result(purchaseService.getMyPurchases())
                .build();
    }

    /**
     * Lấy IP thực của client (xử lý cả trường hợp qua proxy/nginx).
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Nếu có nhiều IP (qua nhiều proxy), lấy IP đầu tiên
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
