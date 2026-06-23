package hokumei.sys.brackeet.controller;

import hokumei.sys.brackeet.common.Result;
import hokumei.sys.brackeet.dto.PaymentIntentRequest;
import hokumei.sys.brackeet.entity.User;
import hokumei.sys.brackeet.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /** 创建支付意图，返回 clientSecret */
    @PostMapping("/create-intent")
    public Result<Map<String, String>> createIntent(
            @RequestBody PaymentIntentRequest request,
            HttpServletRequest httpRequest) {
        User user = (User) httpRequest.getAttribute("currentUser");
        return paymentService.createPaymentIntent(request.getOrderId(), user.getId());
    }

    /** Stripe Webhook 回调（公开接口，不需要认证） */
    @PostMapping("/webhook")
    public Result<Void> webhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        return paymentService.handleWebhook(payload, sigHeader);
    }
}
