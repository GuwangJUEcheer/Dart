package hokumei.sys.brackeet.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import hokumei.sys.brackeet.common.Result;
import hokumei.sys.brackeet.entity.Order;
import hokumei.sys.brackeet.entity.User;
import hokumei.sys.brackeet.mapper.OrderMapper;
import hokumei.sys.brackeet.mapper.UserMapper;
import hokumei.sys.brackeet.service.EmailService;
import hokumei.sys.brackeet.service.PaymentService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${stripe.secret-key}")
    private String secretKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailService emailService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    @Override
    public Result<Map<String, String>> createPaymentIntent(Long orderId, Long userId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.fail("无权操作此订单");
        }
        if (order.getStatus() != 0) {
            return Result.fail("订单状态异常，无法支付");
        }

        try {
            // JPY is a zero-decimal currency in Stripe — send the amount as-is (no x100)
            long amountInYen = order.getTotalAmount().longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInYen)
                    .setCurrency("jpy")
                    .putMetadata("orderId", String.valueOf(orderId))
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            orderMapper.updatePaymentIntentId(orderId, intent.getId());

            Map<String, String> result = new HashMap<>();
            result.put("clientSecret", intent.getClientSecret());
            result.put("paymentIntentId", intent.getId());
            return Result.success(result);

        } catch (StripeException e) {
            return Result.fail("支付初始化失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> handleWebhook(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return Result.fail("签名验证失败");
        }

        if ("payment_intent.succeeded".equals(event.getType())) {
            event.getDataObjectDeserializer().getObject().ifPresent(stripeObject -> {
                PaymentIntent intent = (PaymentIntent) stripeObject;
                String orderIdStr = intent.getMetadata().get("orderId");
                if (orderIdStr != null) {
                    long orderId = Long.parseLong(orderIdStr);
                    orderMapper.updateStatus(orderId, 1);

                    Order order = orderMapper.findById(orderId);
                    if (order != null) {
                        List<hokumei.sys.brackeet.entity.OrderItem> items = orderMapper.findItemsByOrderId(orderId);
                        order.setItems(items);
                        User customer = userMapper.findById(order.getUserId());
                        emailService.sendMerchantOrderNotification(order, customer);
                        emailService.sendCustomerOrderConfirmation(order, customer);
                    }
                }
            });
        }

        return Result.success(null);
    }
}
