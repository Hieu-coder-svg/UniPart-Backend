package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.EmployerPostQuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployerPostQuotaRepository extends JpaRepository<EmployerPostQuota, String> {
    Optional<EmployerPostQuota> findByEmployerId(String employerId);
    Optional<EmployerPostQuota> findByEmployerIdAndQuotaType(String employerId, String quotaType);
    java.util.List<EmployerPostQuota> findAllByEmployerId(String employerId);
}
