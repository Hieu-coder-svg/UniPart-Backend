package com.unipart.unipart_backend.service;

import com.unipart.unipart_backend.configuration.VNPayConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VNPayService {

    private final VNPayConfig vnPayConfig;

    /**
     * Tạo URL thanh toán VNPay.
     *
     * @param amountVnd  Số tiền (VND, chưa nhân 100)
     * @param txnRef     Mã giao dịch duy nhất (transactionRef)
     * @param orderInfo  Mô tả đơn hàng
     * @param ipAddr     IP của client
     * @return URL đầy đủ để redirect sang VNPay
     */
    public String createPaymentUrl(long amountVnd, String txnRef, String orderInfo, String ipAddr) {
        String vnpVersion   = "2.1.0";
        String vnpCommand   = "pay";
        String vnpCurrCode  = "VND";
        String vnpLocale    = "vn";
        String vnpOrderType = "other";

        // VNPay yêu cầu amount * 100
        long vnpAmount = amountVnd * 100;

        // Thời gian tạo và hết hạn (format yyyyMMddHHmmss, timezone Asia/Ho_Chi_Minh)
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String vnpCreateDate = formatter.format(cld.getTime());
        cld.add(Calendar.MINUTE, 15);
        String vnpExpireDate = formatter.format(cld.getTime());

        // Build tham số (dùng TreeMap để auto sort theo key)
        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version",    vnpVersion);
        vnpParams.put("vnp_Command",    vnpCommand);
        vnpParams.put("vnp_TmnCode",    vnPayConfig.getTmnCode());
        vnpParams.put("vnp_Amount",     String.valueOf(vnpAmount));
        vnpParams.put("vnp_CurrCode",   vnpCurrCode);
        vnpParams.put("vnp_TxnRef",     txnRef);
        vnpParams.put("vnp_OrderInfo",  orderInfo);
        vnpParams.put("vnp_OrderType",  vnpOrderType);
        vnpParams.put("vnp_Locale",     vnpLocale);
        vnpParams.put("vnp_ReturnUrl",  vnPayConfig.getReturnUrl());
        vnpParams.put("vnp_IpAddr",     ipAddr);
        vnpParams.put("vnp_CreateDate", vnpCreateDate);
        vnpParams.put("vnp_ExpireDate", vnpExpireDate);

        // Build query string để ký (không encode value khi ký)
        StringBuilder hashData  = new StringBuilder();
        StringBuilder queryStr  = new StringBuilder();

        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                hashData.append(entry.getKey()).append('=')
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII));
                queryStr.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII))
                        .append('=')
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII));
                hashData.append('&');
                queryStr.append('&');
            }
        }
        // Xóa dấu & cuối
        if (!hashData.isEmpty()) hashData.deleteCharAt(hashData.length() - 1);
        if (!queryStr.isEmpty()) queryStr.deleteCharAt(queryStr.length() - 1);

        // Ký HMAC-SHA512
        String vnpSecureHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        queryStr.append("&vnp_SecureHash=").append(vnpSecureHash);

        return vnPayConfig.getUrl() + "?" + queryStr;
    }

    /**
     * Verify chữ ký từ VNPay callback.
     * Lấy tất cả param bắt đầu bằng "vnp_", bỏ vnp_SecureHash và vnp_SecureHashType,
     * sort, build hashData → so sánh với vnp_SecureHash.
     *
     * @param params Toàn bộ query params từ returnUrl
     * @return true nếu chữ ký hợp lệ
     */
    public boolean verifyReturnHash(Map<String, String> params) {
        String vnpSecureHash = params.get("vnp_SecureHash");
        if (vnpSecureHash == null) return false;

        // Sort tất cả params vnp_ trừ SecureHash
        Map<String, String> sortedParams = new TreeMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("vnp_") && !key.equals("vnp_SecureHash") && !key.equals("vnp_SecureHashType")) {
                sortedParams.put(key, entry.getValue());
            }
        }

        // Build hashData
        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                hashData.append(entry.getKey()).append('=')
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII))
                        .append('&');
            }
        }
        if (!hashData.isEmpty()) hashData.deleteCharAt(hashData.length() - 1);

        String computedHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        return computedHash.equalsIgnoreCase(vnpSecureHash);
    }

    /**
     * Tính HMAC-SHA512.
     */
    private String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKey);
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Lỗi khi tính HMAC-SHA512", e);
            throw new RuntimeException("Lỗi khi tính HMAC-SHA512", e);
        }
    }
}
