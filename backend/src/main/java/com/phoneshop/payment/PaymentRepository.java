package com.phoneshop.payment;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
	Optional<PaymentEntity> findByOrderId(Long orderId);
	Optional<PaymentEntity> findByProviderPaymentId(String providerPaymentId);
}
