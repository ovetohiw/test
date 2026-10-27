package com.phoneshop.order;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
	List<OrderEntity> findByUserIdOrderByIdDesc(Long userId);
	List<OrderEntity> findAllByOrderByIdDesc();
	Optional<OrderEntity> findByIdAndUserId(Long id, Long userId);
}
