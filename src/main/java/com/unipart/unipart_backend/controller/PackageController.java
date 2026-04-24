package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.request.PackageRequest;
import com.unipart.unipart_backend.dto.response.ApiResponse;
import com.unipart.unipart_backend.dto.response.PackageResponse;
import com.unipart.unipart_backend.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PackageController {
    private final PackageService packageService;

    @GetMapping("/packages")
    public ApiResponse<List<PackageResponse>> getAll(){
        return ApiResponse.<List<PackageResponse>>builder()
                .result(packageService.getAllPackages())
                .build();
    }

    @GetMapping("/packages/{id}")
    public ApiResponse<PackageResponse> getOne(@PathVariable Long id){
        return ApiResponse.<PackageResponse>builder()
                .result(packageService.getPackageById(id))
                .build();
    }

    @PostMapping("/admin/packages")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PackageResponse> create(@RequestBody PackageRequest packageRequest){
        return ApiResponse.<PackageResponse>builder()
                .result(packageService.createPackage(packageRequest))
                .build();
    }

    @PutMapping("/admin/packages/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PackageResponse> update(@PathVariable Long id, @RequestBody PackageRequest packageRequest){
        return ApiResponse.<PackageResponse>builder()
                .result(packageService.updatePackage(id, packageRequest))
                .build();
    }

    @DeleteMapping("/admin/packages/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        packageService.deletePackage(id);
        return ApiResponse.<Void>builder().build();
    }
}
