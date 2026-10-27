package com.phoneshop.cart;

import com.phoneshop.cart.CartDtos.CartItemRequest;
import com.phoneshop.cart.CartDtos.CartResponse;
import com.phoneshop.cart.CartDtos.UpdateQtyRequest;
import com.phoneshop.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private final CartService cartService;

	public CartController(CartService cartService) {
		this.cartService = cartService;
	}

	@GetMapping
	public CartResponse get() {
		return cartService.getCart(SecurityUtils.requireUser().id());
	}

	@PostMapping("/items")
	public CartResponse add(@Valid @RequestBody CartItemRequest request) {
		return cartService.addItem(SecurityUtils.requireUser().id(), request);
	}

	@PutMapping("/items/{id}")
	public CartResponse update(@PathVariable Long id, @Valid @RequestBody UpdateQtyRequest request) {
		return cartService.updateQty(SecurityUtils.requireUser().id(), id, request.quantity());
	}

	@DeleteMapping("/items/{id}")
	public CartResponse remove(@PathVariable Long id) {
		return cartService.removeItem(SecurityUtils.requireUser().id(), id);
	}
}
