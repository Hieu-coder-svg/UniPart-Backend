package com.unipart.unipart_backend.mapper;

import com.unipart.unipart_backend.dto.response.PurchasePackageResponse;
import com.unipart.unipart_backend.entity.EmployerPackagePurchase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {

    @Mapping(target = "packageName", source = "subscriptionPackage.name")
    @Mapping(target = "packageType", source = "subscriptionPackage.packageType")
    @Mapping(target = "paymentStatus", expression = "java(purchase.getPaymentStatus() != null ? purchase.getPaymentStatus().name() : null)")
    PurchasePackageResponse toResponse(EmployerPackagePurchase purchase);

    List<PurchasePackageResponse> toResponseList(List<EmployerPackagePurchase> purchases);
}
