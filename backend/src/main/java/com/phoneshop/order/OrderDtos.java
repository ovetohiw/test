package com.phoneshop.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public final class OrderDtos {

	private OrderDtos() {
	}

	public record CreateOrderRequest(
			@NotBlank String contactName,
			@NotBlank String contactPhone,
			@NotNull DeliveryMethod deliveryMethod,
			String deliveryAddress
	) {
	}

	public record OrderItemResponse(
			Long productId,
			String productName,
			long unitPriceCents,
			int quantity,
			long lineTotalCents
	) {
	}

	public record OrderResponse(
			Long id,
			OrderStatus status,
			long totalCents,
			String contactName,
			String contactPhone,
			DeliveryMethod deliveryMethod,
			String deliveryAddress,
			Instant createdAt,
			List<OrderItemResponse> items,
			String paymentConfirmationUrl,
			String paymentStatus
	) {
	}
}
