package com.phoneshop.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoneshop.common.ApiException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.yookassa.mode", havingValue = "live")
public class HttpYooKassaClient implements YooKassaClient {

	private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
	private final ObjectMapper objectMapper;
	private final String shopId;
	private final String secretKey;
	private final String apiUrl;

	public HttpYooKassaClient(
			ObjectMapper objectMapper,
			@Value("${app.yookassa.shop-id}") String shopId,
			@Value("${app.yookassa.secret-key}") String secretKey,
			@Value("${app.yookassa.api-url}") String apiUrl
	) {
		this.objectMapper = objectMapper;
		this.shopId = shopId;
		this.secretKey = secretKey;
		this.apiUrl = apiUrl;
	}

	@Override
	public CreatedPayment createPayment(long amountCents, String description, String returnUrl, String idempotenceKey) {
		if (shopId == null || shopId.isBlank() || secretKey == null || secretKey.isBlank()) {
			throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "YooKassa credentials are not configured");
		}
		try {
			Map<String, Object> amount = new LinkedHashMap<>();
			amount.put("value", BigDecimal.valueOf(amountCents, 2).setScale(2, RoundingMode.UNNECESSARY).toPlainString());
			amount.put("currency", "RUB");

			Map<String, Object> confirmation = new LinkedHashMap<>();
			confirmation.put("type", "redirect");
			confirmation.put("return_url", returnUrl);

			Map<String, Object> body = new LinkedHashMap<>();
			body.put("amount", amount);
			body.put("capture", true);
			body.put("confirmation", confirmation);
			body.put("description", description);

			String auth = Base64.getEncoder().encodeToString((shopId + ":" + secretKey).getBytes(StandardCharsets.UTF_8));
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(apiUrl + "/payments"))
					.timeout(Duration.ofSeconds(15))
					.header("Authorization", "Basic " + auth)
					.header("Idempotence-Key", idempotenceKey == null ? UUID.randomUUID().toString() : idempotenceKey)
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
					.build();
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() >= 300) {
				throw new ApiException(HttpStatus.BAD_GATEWAY, "YooKassa payment create failed");
			}
			JsonNode root = objectMapper.readTree(response.body());
			String id = root.path("id").asText(null);
			String status = root.path("status").asText("pending");
			String confirmationUrl = root.path("confirmation").path("confirmation_url").asText(null);
			if (id == null || confirmationUrl == null) {
				throw new ApiException(HttpStatus.BAD_GATEWAY, "YooKassa response incomplete");
			}
			return new CreatedPayment(id, confirmationUrl, status);
		} catch (ApiException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new ApiException(HttpStatus.BAD_GATEWAY, "YooKassa request failed");
		}
	}
}
