package com.phoneshop.order;

import com.phoneshop.order.OrderDtos.OrderResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

	private final OrderService orderService;

	public AdminOrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@GetMapping
	public List<OrderResponse> list() {
		return orderService.listAll();
	}

	@GetMapping("/{id}")
	public OrderResponse get(@PathVariable Long id) {
		return orderService.getAny(id);
	}
}
