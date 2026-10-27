package com.phoneshop.payment;

public interface YooKassaClient {

	CreatedPayment createPayment(long amountCents, String description, String returnUrl, String idempotenceKey);

	record CreatedPayment(String providerPaymentId, String confirmationUrl, String status) {
	}
}
