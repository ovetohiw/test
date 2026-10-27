package com.phoneshop.product;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

	List<ProductEntity> findByActiveTrueOrderByIdDesc();

	List<ProductEntity> findAllByOrderByIdDesc();

	Optional<ProductEntity> findByIdAndActiveTrue(Long id);
}
