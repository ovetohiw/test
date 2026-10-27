package com.phoneshop.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.phoneshop.order.OrderEntity;
import com.phoneshop.order.OrderRepository;
import com.phoneshop.order.OrderStatus;
import com.phoneshop.payment.YooKassaClient.CreatedPayment;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

	@Mock
	PaymentRepository paymentRepository;
	@Mock
	OrderRepository orderRepository;
	@Mock
	YooKassaClient yooKassaClient;

	PaymentService paymentService;

	@BeforeEach
	void setUp() {
		paymentService = new PaymentService(
				paymentRepository,
				orderRepository,
				yooKassaClient,
				"http://localhost:3000",
				"stub"
		);
	}

	@Test
	void createPaymentStoresPendingYooKassaPayment() {
		OrderEntity order = new OrderEntity();
		order.setId(10L);
		order.setTotalCents(10000);
		when(yooKassaClient.createPayment(anyLong(), anyString(), anyString(), anyString()))
				.thenReturn(new CreatedPayment("pay-1", "http://pay", "pending"));
		when(paymentRepository.save(any(PaymentEntity.class))).thenAnswer(inv -> inv.getArgument(0));

		PaymentEntity payment = paymentService.createPaymentForOrder(order);

		assertThat(payment.getProviderPaymentId()).isEqualTo("pay-1");
		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
		assertThat(payment.getConfirmationUrl()).isEqualTo("http://pay");
	}

	@Test
	void webhookMarksOrderPaid() {
		OrderEntity order = new OrderEntity();
		order.setId(5L);
		order.setStatus(OrderStatus.PENDING_PAYMENT);
		PaymentEntity payment = new PaymentEntity();
		payment.setOrder(order);
		payment.setProviderPaymentId("pay-1");
		payment.setStatus(PaymentStatus.PENDING);
		when(paymentRepository.findByProviderPaymentId("pay-1")).thenReturn(Optional.of(payment));

		paymentService.handleYooKassaWebhook("""
				{"event":"payment.succeeded","object":{"id":"pay-1","status":"succeeded"}}
				""");

		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCEEDED);
		assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
		verify(orderRepository).save(order);
	}

	@Test
	void stubCancelDoesNotMarkPaid() {
		OrderEntity order = new OrderEntity();
		order.setId(5L);
		order.setStatus(OrderStatus.PENDING_PAYMENT);
		PaymentEntity payment = new PaymentEntity();
		payment.setOrder(order);
		payment.setProviderPaymentId("stub-1");
		payment.setStatus(PaymentStatus.PENDING);
		when(paymentRepository.findByProviderPaymentId("stub-1")).thenReturn(Optional.of(payment));

		paymentService.completeStubPayment("stub-1", false);

		assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
	}
}
