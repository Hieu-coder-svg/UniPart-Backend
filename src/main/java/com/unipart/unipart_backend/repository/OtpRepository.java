package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Integer> {
    void deleteOtpByEmail(String email);
    Optional<Otp> findFirstByEmailAndIsUsedOrderByCreatedAtDesc(String email, Boolean isUsed);
}
