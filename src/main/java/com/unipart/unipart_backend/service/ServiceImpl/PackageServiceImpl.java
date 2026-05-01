package com.unipart.unipart_backend.service.ServiceImpl;

import com.unipart.unipart_backend.dto.request.PackageRequest;
import com.unipart.unipart_backend.dto.response.PackageResponse;
import com.unipart.unipart_backend.entity.SubscriptionPackage;
import com.unipart.unipart_backend.exception.AppException;
import com.unipart.unipart_backend.exception.ErrorCode;
import com.unipart.unipart_backend.mapper.PackageMapper;
import com.unipart.unipart_backend.repository.SubscriptionPackageRepository;
import com.unipart.unipart_backend.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PackageServiceImpl implements PackageService {

    private final SubscriptionPackageRepository packageRepository;
    private final PackageMapper packageMapper;

    @Override
    public List<PackageResponse> getAllPackages() {
        return packageMapper.toResponseList(packageRepository.findAll());
    }

    @Override
    public PackageResponse getPackageById(Long id) {
        SubscriptionPackage pkg = packageRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PACKAGE_NOT_FOUND));
        return packageMapper.toResponse(pkg);
    }

    @Override
    public PackageResponse createPackage(PackageRequest request) {
        SubscriptionPackage pkg = packageMapper.toEntity(request);
        return packageMapper.toResponse(packageRepository.save(pkg));
    }

    @Override
    public PackageResponse updatePackage(Long id, PackageRequest request) {
        SubscriptionPackage pkg = packageRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PACKAGE_NOT_FOUND));
        packageMapper.updateEntity(pkg, request);
        return packageMapper.toResponse(packageRepository.save(pkg));
    }

    @Override
    public void deletePackage(Long id) {
        if (!packageRepository.existsById(id)) {
            throw new AppException(ErrorCode.PACKAGE_NOT_FOUND);
        }
        packageRepository.deleteById(id);
    }
}
