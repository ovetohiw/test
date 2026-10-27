package com.phoneshop.payment;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.yookassa.mode", havingValue = "stub", matchIfMissing = true)
public class StubYooKassaClient implements YooKassaClient {

	private final String publicBaseUrl;

	public StubYooKassaClient(@Value("${app.public-base-url}") String publicBaseUrl) {
		this.publicBaseUrl = publicBaseUrl;
	}

	@Override
	public CreatedPayment createPayment(long amountCents, String description, String returnUrl, String idempotenceKey) {
		String paymentId = "stub-" + UUID.randomUUID();
		String confirmationUrl = publicBaseUrl + "/api/payments/stub/checkout-page?paymentId=" + paymentId
				+ "&returnUrl=" + java.net.URLEncoder.encode(returnUrl, java.nio.charset.StandardCharsets.UTF_8);
		return new CreatedPayment(paymentId, confirmationUrl, "pending");
	}
}
