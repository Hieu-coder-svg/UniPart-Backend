package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.configuration.VNPayConfig;
import com.unipart.unipart_backend.dto.response.PaymentUrlResponse;
import com.unipart.unipart_backend.dto.response.PurchasePackageResponse;
import com.unipart.unipart_backend.entity.EmployerPackagePurchase;
import com.unipart.unipart_backend.entity.SubscriptionPackage;
import com.unipart.unipart_backend.enums.PaymentStatus;
import com.unipart.unipart_backend.exception.AppException;
import com.unipart.unipart_backend.exception.ErrorCode;
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

import java.math.BigDecimal;
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

    private PurchasePackageResponse toDTO(EmployerPackagePurchase purchase) {
        SubscriptionPackage pkg = purchase.getSubscriptionPackage();
        return PurchasePackageResponse.builder()
                .id(purchase.getId())
                .employerId(purchase.getEmployerId())
                .packageId(purchase.getPackageId())
                .packageName(pkg != null ? pkg.getName() : null)
                .packageType(pkg != null ? pkg.getPackageType() : null)
                .pricePaid(purchase.getPricePaid())
                .paymentStatus(purchase.getPaymentStatus() != null
                        ? purchase.getPaymentStatus().name() : null)
                .transactionRef(purchase.getTransactionRef())
                .purchasedAt(purchase.getPurchasedAt())
                .startDate(purchase.getStartDate())
                .endDate(purchase.getEndDate())
                .tinsPurchased(purchase.getTinsPurchased())
                .build();
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
            return vnPayConfig.getFrontendSuccessUrl()
                    + "?txnRef=" + txnRef
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

    // ===== GET MY PURCHASES =====

    @Override
    public List<PurchasePackageResponse> getMyPurchases() {
        String employerId = getCurrentUserId();
        return purchaseRepository.findAllByEmployerId(employerId)
                .stream()
                .map(this::toDTO)
                .toList();
    }
}
