package com.unipart.unipart_backend.repository;

import com.unipart.unipart_backend.entity.EmployerPackagePurchase;
import com.unipart.unipart_backend.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmployerPackagePurchaseRepository extends JpaRepository<EmployerPackagePurchase, Long> {

    List<EmployerPackagePurchase> findAllByEmployerId(String employerId);

    Optional<EmployerPackagePurchase> findByTransactionRef(String transactionRef);

    @Query("SELECT SUM(e.pricePaid) FROM EmployerPackagePurchase e WHERE e.paymentStatus = :status")
    BigDecimal sumPricePaidByPaymentStatus(@Param("status") PaymentStatus status);

    @Query("SELECT SUM(e.pricePaid) FROM EmployerPackagePurchase e WHERE e.paymentStatus = :status AND e.purchasedAt >= :since")
    BigDecimal sumPricePaidByPaymentStatusSince(@Param("status") PaymentStatus status, @Param("since") LocalDateTime since);

    @Query("SELECT SUM(e.pricePaid) FROM EmployerPackagePurchase e WHERE e.paymentStatus = :status AND e.purchasedAt >= :start AND e.purchasedAt < :end")
    BigDecimal sumPricePaidByPaymentStatusBetween(@Param("status") PaymentStatus status, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
