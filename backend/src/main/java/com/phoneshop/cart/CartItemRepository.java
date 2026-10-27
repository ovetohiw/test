package com.phoneshop.cart;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
	List<CartItemEntity> findByUserIdOrderByIdAsc(Long userId);
	Optional<CartItemEntity> findByUserIdAndProductId(Long userId, Long productId);
	void deleteByUserId(Long userId);
}
