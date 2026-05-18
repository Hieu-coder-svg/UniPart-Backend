package com.unipart.unipart_backend.mapper;

import com.unipart.unipart_backend.dto.response.PurchasePackageResponse;
import com.unipart.unipart_backend.entity.EmployerPackagePurchase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {

    @Mapping(target = "packageName", source = "subscriptionPackage.name")
    @Mapping(target = "packageType", source = "subscriptionPackage.packageType")
    @Mapping(target = "paymentStatus", expression = "java(purchase.getPaymentStatus() != null ? purchase.getPaymentStatus().name() : null)")
    @Mapping(target = "paymentDeadline", source = "paymentDeadline")
    @Mapping(target = "isExpired", expression = "java(isPaymentExpired(purchase))")
    PurchasePackageResponse toResponse(EmployerPackagePurchase purchase);

    default Boolean isPaymentExpired(EmployerPackagePurchase purchase) {
        if (purchase.getPaymentStatus() == com.unipart.unipart_backend.enums.PaymentStatus.PENDING
            && purchase.getPaymentDeadline() != null) {
            return LocalDateTime.now().isAfter(purchase.getPaymentDeadline());
        }
        return false;
    }

    List<PurchasePackageResponse> toResponseList(List<EmployerPackagePurchase> purchases);
}
