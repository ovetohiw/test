package com.phoneshop.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoneshop.common.ApiException;
import com.phoneshop.order.OrderEntity;
import com.phoneshop.order.OrderRepository;
import com.phoneshop.order.OrderStatus;
import com.phoneshop.payment.YooKassaClient.CreatedPayment;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;
	private final YooKassaClient yooKassaClient;
	private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
	private final String frontendBaseUrl;
	private final String yookassaMode;

	public PaymentService(
			PaymentRepository paymentRepository,
			OrderRepository orderRepository,
			YooKassaClient yooKassaClient,
			@Value("${app.frontend-base-url}") String frontendBaseUrl,
			@Value("${app.yookassa.mode:stub}") String yookassaMode
	) {
		this.paymentRepository = paymentRepository;
		this.orderRepository = orderRepository;
		this.yooKassaClient = yooKassaClient;
		this.frontendBaseUrl = frontendBaseUrl;
		this.yookassaMode = yookassaMode;
	}

	@Transactional
	public PaymentEntity createPaymentForOrder(OrderEntity order) {
		String returnUrl = frontendBaseUrl + "/orders/" + order.getId();
		CreatedPayment created = yooKassaClient.createPayment(
				order.getTotalCents(),
				"Order #" + order.getId(),
				returnUrl,
				UUID.randomUUID().toString()
		);
		PaymentEntity payment = new PaymentEntity();
		payment.setOrder(order);
		payment.setProvider("yookassa");
		payment.setProviderPaymentId(created.providerPaymentId());
		payment.setAmountCents(order.getTotalCents());
		payment.setConfirmationUrl(created.confirmationUrl());
		payment.setStatus(PaymentStatus.PENDING);
		return paymentRepository.save(payment);
	}

	@Transactional
	public void handleYooKassaWebhook(String rawBody) {
		try {
			JsonNode root = objectMapper.readTree(rawBody);
			JsonNode object = root.path("object");
			String providerPaymentId = object.path("id").asText(null);
			String status = object.path("status").asText(null);
			if (providerPaymentId == null || status == null) {
				throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid webhook payload");
			}
			applyProviderStatus(providerPaymentId, status);
		} catch (ApiException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid webhook payload");
		}
	}

	@Transactional
	public String completeStubPayment(String paymentId, boolean success) {
		PaymentEntity payment = paymentRepository.findByProviderPaymentId(paymentId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Payment not found"));
		if (!"stub".equalsIgnoreCase(yookassaMode) && !paymentId.startsWith("stub-")) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Stub checkout is disabled");
		}
		applyProviderStatus(paymentId, success ? "succeeded" : "canceled");
		return frontendBaseUrl + "/orders/" + payment.getOrder().getId();
	}

	private void applyProviderStatus(String providerPaymentId, String status) {
		PaymentEntity payment = paymentRepository.findByProviderPaymentId(providerPaymentId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Payment not found"));
		if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
			return;
		}
		OrderEntity order = payment.getOrder();
		if ("succeeded".equalsIgnoreCase(status)) {
			payment.setStatus(PaymentStatus.SUCCEEDED);
			payment.setUpdatedAt(Instant.now());
			order.setStatus(OrderStatus.PAID);
			order.setUpdatedAt(Instant.now());
			orderRepository.save(order);
			paymentRepository.save(payment);
			return;
		}
		if ("canceled".equalsIgnoreCase(status) || "cancelled".equalsIgnoreCase(status)) {
			payment.setStatus(PaymentStatus.CANCELLED);
			payment.setUpdatedAt(Instant.now());
			if (order.getStatus() != OrderStatus.PAID) {
				order.setStatus(OrderStatus.CANCELLED);
				order.setUpdatedAt(Instant.now());
				orderRepository.save(order);
			}
			paymentRepository.save(payment);
		}
	}
}
