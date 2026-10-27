package com.phoneshop.product;

import com.phoneshop.common.ApiException;
import com.phoneshop.product.ProductDtos.ProductRequest;
import com.phoneshop.product.ProductDtos.ProductResponse;
import com.phoneshop.storage.FileStorageService;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final FileStorageService fileStorageService;
	private final String publicBaseUrl;

	public ProductService(
			ProductRepository productRepository,
			FileStorageService fileStorageService,
			@Value("${app.public-base-url}") String publicBaseUrl
	) {
		this.productRepository = productRepository;
		this.fileStorageService = fileStorageService;
		this.publicBaseUrl = publicBaseUrl;
	}

	@Transactional(readOnly = true)
	public List<ProductResponse> list(String brand, Long minPrice, Long maxPrice, boolean includeInactive) {
		String brandFilter = emptyToNull(brand);
		List<ProductEntity> products = includeInactive
				? productRepository.findAllByOrderByIdDesc()
				: productRepository.findByActiveTrueOrderByIdDesc();
		return products.stream()
				.filter(product -> brandFilter == null || product.getBrand().equalsIgnoreCase(brandFilter))
				.filter(product -> minPrice == null || product.getPriceCents() >= minPrice)
				.filter(product -> maxPrice == null || product.getPriceCents() <= maxPrice)
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public ProductResponse getPublic(Long id) {
		ProductEntity product = productRepository.findByIdAndActiveTrue(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found"));
		return toResponse(product);
	}

	@Transactional(readOnly = true)
	public ProductEntity requireActiveForPurchase(Long id) {
		ProductEntity product = productRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found"));
		if (!product.isActive()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Product is not available");
		}
		return product;
	}

	@Transactional(readOnly = true)
	public ProductEntity requireAny(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Product not found"));
	}

	@Transactional
	public ProductResponse create(ProductRequest request) {
		ProductEntity product = new ProductEntity();
		apply(product, request);
		return toResponse(productRepository.save(product));
	}

	@Transactional
	public ProductResponse update(Long id, ProductRequest request) {
		ProductEntity product = requireAny(id);
		apply(product, request);
		product.setUpdatedAt(Instant.now());
		return toResponse(productRepository.save(product));
	}

	@Transactional
	public ProductResponse setActive(Long id, boolean active) {
		ProductEntity product = requireAny(id);
		product.setActive(active);
		product.setUpdatedAt(Instant.now());
		return toResponse(productRepository.save(product));
	}

	@Transactional
	public ProductResponse uploadImage(Long id, MultipartFile file) {
		ProductEntity product = requireAny(id);
		String path = fileStorageService.storeProductImage(id, file);
		product.setImagePath(path);
		product.setUpdatedAt(Instant.now());
		return toResponse(productRepository.save(product));
	}

	private void apply(ProductEntity product, ProductRequest request) {
		product.setName(request.name().trim());
		product.setBrand(request.brand().trim());
		product.setDescription(request.description() == null ? "" : request.description());
		product.setPriceCents(request.priceCents());
		product.setStock(request.stock());
		product.setSpecsJson(request.specsJson() == null || request.specsJson().isBlank() ? "{}" : request.specsJson());
		if (request.active() != null) {
			product.setActive(request.active());
		}
	}

	private ProductResponse toResponse(ProductEntity product) {
		String imageUrl = product.getImagePath() == null ? null : publicBaseUrl + "/uploads/" + product.getImagePath();
		return new ProductResponse(
				product.getId(),
				product.getName(),
				product.getBrand(),
				product.getDescription(),
				product.getPriceCents(),
				product.getStock(),
				product.getSpecsJson(),
				imageUrl,
				product.isActive()
		);
	}

	private String emptyToNull(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}
}
