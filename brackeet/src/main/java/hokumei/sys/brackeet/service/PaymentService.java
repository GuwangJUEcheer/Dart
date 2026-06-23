package hokumei.sys.brackeet.service;

import hokumei.sys.brackeet.common.Result;

import java.util.Map;

public interface PaymentService {

    Result<Map<String, String>> createPaymentIntent(Long orderId, Long userId);

    Result<Void> handleWebhook(String payload, String sigHeader);
}
