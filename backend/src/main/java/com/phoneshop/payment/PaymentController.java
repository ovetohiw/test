package com.phoneshop.payment;

import java.net.URI;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@PostMapping(path = "/yookassa/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> yookassaWebhook(@RequestBody String body) {
		paymentService.handleYooKassaWebhook(body);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/stub/checkout")
	public ResponseEntity<Void> stubCheckout(
			@RequestParam String paymentId,
			@RequestParam(defaultValue = "true") boolean success,
			@RequestParam(required = false) String returnUrl
	) {
		String redirect = paymentService.completeStubPayment(paymentId, success);
		if (returnUrl != null && !returnUrl.isBlank()) {
			redirect = returnUrl;
		}
		return ResponseEntity.status(302).location(URI.create(redirect)).build();
	}

	@GetMapping(path = "/stub/checkout-page", produces = MediaType.TEXT_HTML_VALUE)
	public String stubPage(@RequestParam String paymentId, @RequestParam(required = false) String returnUrl) {
		String encodedReturn = returnUrl == null ? "" : java.net.URLEncoder.encode(returnUrl, java.nio.charset.StandardCharsets.UTF_8);
		String suffix = encodedReturn.isBlank() ? "" : "&returnUrl=" + encodedReturn;
		return """
				<!DOCTYPE html>
				<html lang="ru"><head><meta charset="utf-8"><title>Stub YooKassa</title></head>
				<body style="font-family:sans-serif;padding:2rem">
				  <h1>Тестовая оплата ЮKassa</h1>
				  <p>Payment: %s</p>
				  <p><a href="/api/payments/stub/checkout?paymentId=%s&success=true%s">Оплатить успешно</a></p>
				  <p><a href="/api/payments/stub/checkout?paymentId=%s&success=false%s">Отменить оплату</a></p>
				</body></html>
				""".formatted(paymentId, paymentId, suffix, paymentId, suffix);
	}

	@PostMapping("/stub/simulate")
	public Map<String, String> simulate(@RequestBody Map<String, Object> body) {
		String paymentId = String.valueOf(body.get("paymentId"));
		boolean success = body.get("success") == null || Boolean.parseBoolean(String.valueOf(body.get("success")));
		String redirect = paymentService.completeStubPayment(paymentId, success);
		return Map.of("redirectUrl", redirect);
	}
}
