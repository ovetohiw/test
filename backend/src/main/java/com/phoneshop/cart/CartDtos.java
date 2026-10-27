package com.phoneshop.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public final class CartDtos {

	private CartDtos() {
	}

	public record CartItemRequest(@NotNull Long productId, @Min(1) int quantity) {
	}

	public record UpdateQtyRequest(@Min(1) int quantity) {
	}

	public record CartItemResponse(
			Long id,
			Long productId,
			String productName,
			String brand,
			long unitPriceCents,
			int quantity,
			int stock,
			String imageUrl,
			long lineTotalCents
	) {
	}

	public record CartResponse(List<CartItemResponse> items, long totalCents) {
	}
}
