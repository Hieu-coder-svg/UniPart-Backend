package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.request.PackageRequest;
import com.unipart.unipart_backend.dto.response.PackageResponse;
import com.unipart.unipart_backend.entity.SubscriptionPackage;
import com.unipart.unipart_backend.exception.AppException;
import com.unipart.unipart_backend.exception.ErrorCode;
import com.unipart.unipart_backend.repository.SubscriptionPackageRepository;
import com.unipart.unipart_backend.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PackageServiceImpl implements PackageService {

    private final SubscriptionPackageRepository packageRepository;

    private PackageResponse toDTO(SubscriptionPackage pkg) {
        return PackageResponse.builder()
                .id(pkg.getId())
                .name(pkg.getName())
                .packageType(pkg.getPackageType())
                .price(pkg.getPrice())
                .description(pkg.getDescription())
                .durationDays(pkg.getDurationDays())
                .normalTinsLimit(pkg.getNormalTinsLimit())
                .maxNormalTinsPerDay(pkg.getMaxNormalTinsPerDay())
                .urgentTinsLimit(pkg.getUrgentTinsLimit())
                .tinType(pkg.getTinType())
                .tinQuantity(pkg.getTinQuantity())
                .createdAt(pkg.getCreatedAt())
                .updatedAt(pkg.getUpdatedAt())
                .build();
    }
    @Override
    public List<PackageResponse> getAllPackages() {
        return packageRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public PackageResponse getPackageById(Long id) {
        return packageRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new AppException(ErrorCode.PACKAGE_NOT_FOUND));
    }

    @Override
    public PackageResponse createPackage(PackageRequest request) {
        SubscriptionPackage pkg = SubscriptionPackage.builder()
                .name(request.getName())
                .packageType(request.getPackageType())
                .price(request.getPrice())
                .description(request.getDescription())
                .durationDays(request.getDurationDays())
                .normalTinsLimit(request.getNormalTinsLimit())
                .maxNormalTinsPerDay(request.getMaxNormalTinsPerDay())
                .urgentTinsLimit(request.getUrgentTinsLimit())
                .tinType(request.getTinType())
                .tinQuantity(request.getTinQuantity())
                .build();
        return toDTO(packageRepository.save(pkg));
    }

    @Override
    public PackageResponse updatePackage(Long id, PackageRequest request) {
        SubscriptionPackage pkg = packageRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PACKAGE_NOT_FOUND));
        pkg.setName(request.getName());
        pkg.setPackageType(request.getPackageType());
        pkg.setPrice(request.getPrice());
        pkg.setDescription(request.getDescription());
        pkg.setDurationDays(request.getDurationDays());
        pkg.setNormalTinsLimit(request.getNormalTinsLimit());
        pkg.setMaxNormalTinsPerDay(request.getMaxNormalTinsPerDay());
        pkg.setUrgentTinsLimit(request.getUrgentTinsLimit());
        pkg.setTinType(request.getTinType());
        pkg.setTinQuantity(request.getTinQuantity());
        return toDTO(packageRepository.save(pkg));
    }

    @Override
    public void deletePackage(Long id) {
        if (!packageRepository.existsById(id)) {
            throw new AppException(ErrorCode.PACKAGE_NOT_FOUND);
        }
        packageRepository.deleteById(id);
    }
}
