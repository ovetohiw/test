package com.phoneshop.cart;

import com.phoneshop.cart.CartDtos.CartItemRequest;
import com.phoneshop.cart.CartDtos.CartItemResponse;
import com.phoneshop.cart.CartDtos.CartResponse;
import com.phoneshop.common.ApiException;
import com.phoneshop.product.ProductEntity;
import com.phoneshop.product.ProductService;
import com.phoneshop.user.UserEntity;
import com.phoneshop.user.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

	private final CartItemRepository cartItemRepository;
	private final UserRepository userRepository;
	private final ProductService productService;
	private final String publicBaseUrl;

	public CartService(
			CartItemRepository cartItemRepository,
			UserRepository userRepository,
			ProductService productService,
			@Value("${app.public-base-url}") String publicBaseUrl
	) {
		this.cartItemRepository = cartItemRepository;
		this.userRepository = userRepository;
		this.productService = productService;
		this.publicBaseUrl = publicBaseUrl;
	}

	@Transactional(readOnly = true)
	public CartResponse getCart(Long userId) {
		List<CartItemResponse> items = new ArrayList<>();
		long total = 0;
		for (CartItemEntity item : cartItemRepository.findByUserIdOrderByIdAsc(userId)) {
			CartItemResponse response = toResponse(item);
			items.add(response);
			total += response.lineTotalCents();
		}
		return new CartResponse(items, total);
	}

	@Transactional
	public CartResponse addItem(Long userId, CartItemRequest request) {
		ProductEntity product = productService.requireActiveForPurchase(request.productId());
		ensureStock(product, request.quantity());
		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
		CartItemEntity item = cartItemRepository.findByUserIdAndProductId(userId, product.getId())
				.orElseGet(() -> {
					CartItemEntity created = new CartItemEntity();
					created.setUser(user);
					created.setProduct(product);
					created.setQuantity(0);
					return created;
				});
		int newQty = item.getQuantity() + request.quantity();
		ensureStock(product, newQty);
		item.setQuantity(newQty);
		cartItemRepository.save(item);
		return getCart(userId);
	}

	@Transactional
	public CartResponse updateQty(Long userId, Long itemId, int quantity) {
		CartItemEntity item = requireOwnedItem(userId, itemId);
		ensureStock(item.getProduct(), quantity);
		item.setQuantity(quantity);
		cartItemRepository.save(item);
		return getCart(userId);
	}

	@Transactional
	public CartResponse removeItem(Long userId, Long itemId) {
		CartItemEntity item = requireOwnedItem(userId, itemId);
		cartItemRepository.delete(item);
		return getCart(userId);
	}

	@Transactional(readOnly = true)
	public List<CartItemEntity> requireNonEmptyCart(Long userId) {
		List<CartItemEntity> items = cartItemRepository.findByUserIdOrderByIdAsc(userId);
		if (items.isEmpty()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Cart is empty");
		}
		return items;
	}

	@Transactional
	public void clear(Long userId) {
		cartItemRepository.deleteByUserId(userId);
	}

	private CartItemEntity requireOwnedItem(Long userId, Long itemId) {
		CartItemEntity item = cartItemRepository.findById(itemId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cart item not found"));
		if (!item.getUser().getId().equals(userId)) {
			throw new ApiException(HttpStatus.FORBIDDEN, "Cart item not found");
		}
		return item;
	}

	private void ensureStock(ProductEntity product, int quantity) {
		if (!product.isActive()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Product is not available");
		}
		if (quantity > product.getStock()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Not enough stock for " + product.getName());
		}
	}

	private CartItemResponse toResponse(CartItemEntity item) {
		ProductEntity product = item.getProduct();
		String imageUrl = product.getImagePath() == null ? null : publicBaseUrl + "/uploads/" + product.getImagePath();
		long line = product.getPriceCents() * item.getQuantity();
		return new CartItemResponse(
				item.getId(),
				product.getId(),
				product.getName(),
				product.getBrand(),
				product.getPriceCents(),
				item.getQuantity(),
				product.getStock(),
				imageUrl,
				line
		);
	}
}
