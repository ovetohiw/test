package com.phoneshop.order;

import com.phoneshop.cart.CartItemEntity;
import com.phoneshop.cart.CartService;
import com.phoneshop.common.ApiException;
import com.phoneshop.order.OrderDtos.CreateOrderRequest;
import com.phoneshop.order.OrderDtos.OrderItemResponse;
import com.phoneshop.order.OrderDtos.OrderResponse;
import com.phoneshop.payment.PaymentEntity;
import com.phoneshop.payment.PaymentRepository;
import com.phoneshop.payment.PaymentService;
import com.phoneshop.product.ProductEntity;
import com.phoneshop.user.UserEntity;
import com.phoneshop.user.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final CartService cartService;
	private final PaymentService paymentService;
	private final PaymentRepository paymentRepository;

	public OrderService(
			OrderRepository orderRepository,
			UserRepository userRepository,
			CartService cartService,
			PaymentService paymentService,
			PaymentRepository paymentRepository
	) {
		this.orderRepository = orderRepository;
		this.userRepository = userRepository;
		this.cartService = cartService;
		this.paymentService = paymentService;
		this.paymentRepository = paymentRepository;
	}

	@Transactional
	public OrderResponse create(Long userId, CreateOrderRequest request) {
		if (request.deliveryMethod() == DeliveryMethod.DELIVERY
				&& (request.deliveryAddress() == null || request.deliveryAddress().isBlank())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Delivery address is required");
		}
		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
		List<CartItemEntity> cartItems = cartService.requireNonEmptyCart(userId);

		OrderEntity order = new OrderEntity();
		order.setUser(user);
		order.setStatus(OrderStatus.PENDING_PAYMENT);
		order.setContactName(request.contactName().trim());
		order.setContactPhone(request.contactPhone().trim());
		order.setDeliveryMethod(request.deliveryMethod());
		order.setDeliveryAddress(request.deliveryAddress() == null ? null : request.deliveryAddress().trim());

		long total = 0;
		for (CartItemEntity cartItem : cartItems) {
			ProductEntity product = cartItem.getProduct();
			if (!product.isActive()) {
				throw new ApiException(HttpStatus.BAD_REQUEST, "Product is not available: " + product.getName());
			}
			if (cartItem.getQuantity() > product.getStock()) {
				throw new ApiException(HttpStatus.BAD_REQUEST, "Not enough stock for " + product.getName());
			}
			OrderItemEntity item = new OrderItemEntity();
			item.setOrder(order);
			item.setProduct(product);
			item.setProductName(product.getName());
			item.setUnitPriceCents(product.getPriceCents());
			item.setQuantity(cartItem.getQuantity());
			order.getItems().add(item);
			total += product.getPriceCents() * cartItem.getQuantity();
			product.setStock(product.getStock() - cartItem.getQuantity());
		}
		order.setTotalCents(total);
		orderRepository.save(order);
		cartService.clear(userId);

		PaymentEntity payment = paymentService.createPaymentForOrder(order);
		return toResponse(order, payment);
	}

	@Transactional(readOnly = true)
	public OrderResponse getForUser(Long userId, Long orderId) {
		OrderEntity order = orderRepository.findByIdAndUserId(orderId, userId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Order not found"));
		PaymentEntity payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
		return toResponse(order, payment);
	}

	@Transactional(readOnly = true)
	public List<OrderResponse> listForUser(Long userId) {
		return orderRepository.findByUserIdOrderByIdDesc(userId).stream()
				.map(order -> toResponse(order, paymentRepository.findByOrderId(order.getId()).orElse(null)))
				.toList();
	}

	@Transactional(readOnly = true)
	public List<OrderResponse> listAll() {
		return orderRepository.findAllByOrderByIdDesc().stream()
				.map(order -> toResponse(order, paymentRepository.findByOrderId(order.getId()).orElse(null)))
				.toList();
	}

	@Transactional(readOnly = true)
	public OrderResponse getAny(Long orderId) {
		OrderEntity order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Order not found"));
		return toResponse(order, paymentRepository.findByOrderId(order.getId()).orElse(null));
	}

	private OrderResponse toResponse(OrderEntity order, PaymentEntity payment) {
		List<OrderItemResponse> items = order.getItems().stream()
				.map(item -> new OrderItemResponse(
						item.getProduct().getId(),
						item.getProductName(),
						item.getUnitPriceCents(),
						item.getQuantity(),
						item.getUnitPriceCents() * item.getQuantity()
				))
				.toList();
		return new OrderResponse(
				order.getId(),
				order.getStatus(),
				order.getTotalCents(),
				order.getContactName(),
				order.getContactPhone(),
				order.getDeliveryMethod(),
				order.getDeliveryAddress(),
				order.getCreatedAt(),
				items,
				payment == null ? null : payment.getConfirmationUrl(),
				payment == null ? null : payment.getStatus().name()
		);
	}
}
