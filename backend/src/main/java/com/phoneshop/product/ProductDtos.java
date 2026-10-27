package com.phoneshop.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class ProductDtos {

	private ProductDtos() {
	}

	public record ProductResponse(
			Long id,
			String name,
			String brand,
			String description,
			long priceCents,
			int stock,
			String specsJson,
			String imageUrl,
			boolean active
	) {
	}

	public record ProductRequest(
			@NotBlank @Size(max = 255) String name,
			@NotBlank @Size(max = 128) String brand,
			@NotNull String description,
			@Min(0) long priceCents,
			@Min(0) int stock,
			@NotNull String specsJson,
			Boolean active
	) {
	}
}
