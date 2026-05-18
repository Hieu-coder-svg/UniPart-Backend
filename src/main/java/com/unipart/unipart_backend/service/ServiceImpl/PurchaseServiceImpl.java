package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.configuration.VNPayConfig;
import com.unipart.unipart_backend.dto.response.PaymentUrlResponse;
import com.unipart.unipart_backend.dto.response.PurchasePackageResponse;
import com.unipart.unipart_backend.entity.EmployerPackagePurchase;
import com.unipart.unipart_backend.entity.SubscriptionPackage;
import com.unipart.unipart_backend.enums.PaymentStatus;
import com.unipart.unipart_backend.exception.AppException;
import com.unipart.unipart_backend.exception.ErrorCode;
import com.unipart.unipart_backend.mapper.PurchaseMapper;
import com.unipart.unipart_backend.repository.EmployerPackagePurchaseRepository;
import com.unipart.unipart_backend.repository.SubscriptionPackageRepository;
import com.unipart.unipart_backend.service.PurchaseService;
import com.unipart.unipart_backend.service.VNPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import com.unipart.unipart_backend.repository.UserRepository;
import com.unipart.unipart_backend.entity.User;
import org.springframework.beans.factory.annotation.Value;
import java.io.UnsupportedEncodingException;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseServiceImpl implements PurchaseService {

    private final SubscriptionPackageRepository subscriptionPackageRepository;
    private final EmployerPackagePurchaseRepository purchaseRepository;
    private final VNPayService vnPayService;
    private final VNPayConfig vnPayConfig;
    private final PurchaseMapper purchaseMapper;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // ===== Helper =====

    /**
     * Lấy userId (UUID) từ JWT claim "userId", giống pattern trong ApplicationServiceImpl.
     */
    private String getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("userId");
        }
        return authentication.getName();
    }

    // ===== CREATE PAYMENT URL =====

    @Override
    @Transactional
    public PaymentUrlResponse createPaymentUrl(Long packageId, String ipAddress) {
        String employerId = getCurrentUserId();

        // Kiểm tra gói tồn tại
        SubscriptionPackage pkg = subscriptionPackageRepository.findById(packageId)
                .orElseThrow(() -> new AppException(ErrorCode.PACKAGE_NOT_FOUND));

        // Tạo transactionRef ngắn gọn (tối đa 15 ký tự theo giới hạn VNPay)
        String txnRef = UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        // Ghi bản ghi PENDING vào DB trước khi redirect sang VNPay
        EmployerPackagePurchase purchase = EmployerPackagePurchase.builder()
                .employerId(employerId)
                .packageId(packageId)
                .purchasedAt(LocalDateTime.now())
                .pricePaid(pkg.getPrice())
                .paymentStatus(PaymentStatus.PENDING)
                .transactionRef(txnRef)
                .build();
        purchaseRepository.save(purchase);

        // Tạo URL thanh toán VNPay
        long amountVnd = pkg.getPrice().longValue();
        String orderInfo = "Employer " + employerId + " mua goi " + pkg.getName();
        String paymentUrl = vnPayService.createPaymentUrl(amountVnd, txnRef, orderInfo, ipAddress);

        log.info("Tạo payment URL cho employer={}, package={}, txnRef={}", employerId, packageId, txnRef);

        return PaymentUrlResponse.builder()
                .paymentUrl(paymentUrl)
                .transactionRef(txnRef)
                .build();
    }

    // ===== HANDLE VNPAY RETURN =====

    @Override
    @Transactional
    public String handleVNPayReturn(Map<String, String> params) {
        // 1. Verify chữ ký
        if (!vnPayService.verifyReturnHash(params)) {
            log.warn("VNPay callback chữ ký không hợp lệ: {}", params);
            return vnPayConfig.getFrontendFailUrl() + "?error=invalid_signature";
        }

        // 2. Tìm bản ghi theo transactionRef
        String txnRef = params.get("vnp_TxnRef");
        EmployerPackagePurchase purchase = purchaseRepository.findByTransactionRef(txnRef)
                .orElseThrow(() -> new AppException(ErrorCode.PURCHASE_NOT_FOUND));

        String responseCode = params.get("vnp_ResponseCode");

        if ("00".equals(responseCode)) {
            // Thanh toán thành công
            SubscriptionPackage pkg = purchase.getSubscriptionPackage();

            // Tính endDate nếu là gói MONTHLY có durationDays
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = null;
            if (pkg != null && pkg.getDurationDays() != null) {
                endDate = startDate.plusDays(pkg.getDurationDays());
            }

            // Số TIN nếu là gói TIN
            Integer tinsPurchased = null;
            if (pkg != null && "TIN".equalsIgnoreCase(pkg.getPackageType())) {
                tinsPurchased = pkg.getTinQuantity();
            }

            purchase.setPaymentStatus(PaymentStatus.SUCCESS);
            purchase.setStartDate(startDate);
            purchase.setEndDate(endDate);
            purchase.setTinsPurchased(tinsPurchased);
            purchaseRepository.save(purchase);

            log.info("Thanh toán thành công txnRef={}", txnRef);

            // Gửi email thông báo mua thành công
            sendPurchaseSuccessEmail(purchase.getEmployerId(), pkg.getName(), purchase.getPricePaid().toString());

            return vnPayConfig.getFrontendSuccessUrl()
                    + "?success=true"
                    + "&txnRef=" + txnRef
                    + "&packageId=" + purchase.getPackageId();
        } else {
            // Thanh toán thất bại
            purchase.setPaymentStatus(PaymentStatus.FAILED);
            purchaseRepository.save(purchase);

            log.info("Thanh toán thất bại txnRef={}, responseCode={}", txnRef, responseCode);
            return vnPayConfig.getFrontendFailUrl()
                    + "?txnRef=" + txnRef
                    + "&code=" + responseCode;
        }
    }

    private void sendPurchaseSuccessEmail(String employerId, String packageName, String price) {
        try {
            User user = userRepository.findById(employerId).orElse(null);
            if (user == null || user.getEmail() == null) {
                log.warn("Cannot send purchase email: User or email not found for employerId {}", employerId);
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setSubject("Xác nhận thanh toán thành công tại Unipart");

            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                    <h2 style="color: #27ae60;">Thanh toán thành công!</h2>
                    <p>Xin chào <strong>%s</strong>,</p>
                    <p>Cảm ơn bạn đã tin tưởng và sử dụng dịch vụ của Unipart.</p>
                    <p>Chúng tôi xin thông báo giao dịch mua gói dịch vụ của bạn đã được thực hiện thành công với thông tin như sau:</p>
                    <ul style="background-color: #f9f9f9; padding: 15px; border-radius: 5px; list-style-type: none;">
                        <li><strong>Tên gói:</strong> %s</li>
                        <li><strong>Số tiền thanh toán:</strong> %s VND</li>
                    </ul>
                    <p>Gói dịch vụ đã được kích hoạt và sẵn sàng sử dụng.</p>
                    <p>Nếu bạn có bất kỳ thắc mắc nào, vui lòng liên hệ với bộ phận hỗ trợ của chúng tôi.</p>
                    <hr style="margin: 20px 0;">
                    <p style="color: #7f8c8d; font-size: 14px;">Trân trọng,<br><strong>Unipart Team</strong></p>
                </div>
                """.formatted(user.getFullName(), packageName, price);

            helper.setText(htmlContent, true);
            helper.setFrom(fromEmail, "Unipart");

            mailSender.send(message);
            log.info("Đã gửi email thông báo thanh toán thành công cho user {}", user.getEmail());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Không thể gửi email thanh toán thành công: {}", e.getMessage(), e);
        }
    }

    // ===== GET MY PURCHASES =====

    @Override
    public List<PurchasePackageResponse> getMyPurchases() {
        String employerId = getCurrentUserId();
        return purchaseMapper.toResponseList(
                purchaseRepository.findAllByEmployerId(employerId));
    }
}
