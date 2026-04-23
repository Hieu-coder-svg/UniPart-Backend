package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.EmployerPackagePurchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployerPackagePurchaseRepository extends JpaRepository<EmployerPackagePurchase, Long> {

    List<EmployerPackagePurchase> findAllByEmployerId(String employerId);

    Optional<EmployerPackagePurchase> findByTransactionRef(String transactionRef);
}
