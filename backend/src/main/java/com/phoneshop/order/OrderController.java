package com.phoneshop.order;

import com.phoneshop.order.OrderDtos.CreateOrderRequest;
import com.phoneshop.order.OrderDtos.OrderResponse;
import com.phoneshop.security.SecurityUtils;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
		return orderService.create(SecurityUtils.requireUser().id(), request);
	}

	@GetMapping
	public List<OrderResponse> listMine() {
		return orderService.listForUser(SecurityUtils.requireUser().id());
	}

	@GetMapping("/{id}")
	public OrderResponse get(@PathVariable Long id) {
		return orderService.getForUser(SecurityUtils.requireUser().id(), id);
	}
}
