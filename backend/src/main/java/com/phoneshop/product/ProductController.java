package com.phoneshop.product;

import com.phoneshop.product.ProductDtos.ProductResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	public List<ProductResponse> list(
			@RequestParam(required = false) String brand,
			@RequestParam(required = false) Long minPrice,
			@RequestParam(required = false) Long maxPrice
	) {
		return productService.list(brand, minPrice, maxPrice, false);
	}

	@GetMapping("/{id}")
	public ProductResponse get(@PathVariable Long id) {
		return productService.getPublic(id);
	}
}
