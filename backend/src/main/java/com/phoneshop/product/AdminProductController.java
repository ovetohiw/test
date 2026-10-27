package com.phoneshop.product;

import com.phoneshop.product.ProductDtos.ProductRequest;
import com.phoneshop.product.ProductDtos.ProductResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

	private final ProductService productService;

	public AdminProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	public List<ProductResponse> list(
			@RequestParam(required = false) String brand,
			@RequestParam(required = false) Long minPrice,
			@RequestParam(required = false) Long maxPrice
	) {
		return productService.list(brand, minPrice, maxPrice, true);
	}

	@PostMapping
	public ProductResponse create(@Valid @RequestBody ProductRequest request) {
		return productService.create(request);
	}

	@PutMapping("/{id}")
	public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
		return productService.update(id, request);
	}

	@PostMapping("/{id}/active")
	public ProductResponse setActive(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
		Boolean active = body.get("active");
		return productService.setActive(id, active != null && active);
	}

	@PostMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ProductResponse uploadImage(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
		return productService.uploadImage(id, file);
	}
}
