package hokumei.sys.brackeet.controller;

import hokumei.sys.brackeet.common.Result;
import hokumei.sys.brackeet.entity.Order;
import hokumei.sys.brackeet.entity.OrderItem;
import hokumei.sys.brackeet.entity.User;
import hokumei.sys.brackeet.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    /**
     * Admin-only test endpoint. Sends a mock order email so you can verify
     * Gmail SMTP config without making a real payment.
     *
     * Usage: POST /api/email/test?customerEmail=you@example.com
     */
    @PostMapping("/test")
    public Result<String> sendTestEmail(
            @RequestParam(defaultValue = "") String customerEmail) {

        // Build a fake order with one item
        OrderItem item = new OrderItem();
        item.setId(1L);
        item.setOrderId(999L);
        item.setProductId(1L);
        item.setProductName("カーネリアンブレスレット【テスト】");
        item.setProductImage("https://cdn.shopify.com/s/files/1/0636/7500/7221/files/b024001.jpg");
        item.setPrice(new BigDecimal("11300"));
        item.setQuantity(2);

        Order order = new Order();
        order.setId(999L);
        order.setUserId(1L);
        order.setTotalAmount(new BigDecimal("22600"));
        order.setStatus(1);
        order.setReceiverName("テスト 太郎");
        order.setReceiverPhone("090-0000-0000");
        order.setReceiverAddress("東京都渋谷区テスト1-2-3");
        order.setCreateTime(LocalDateTime.now());
        order.setItems(List.of(item));

        User customer = new User();
        customer.setId(1L);
        customer.setNickname("テストユーザー");
        customer.setEmail(customerEmail.isBlank() ? null : customerEmail);

        emailService.sendMerchantOrderNotification(order, customer);
        emailService.sendCustomerOrderConfirmation(order, customer);

        String msg = "商家通知邮件已发送" +
                (customerEmail.isBlank() ? "（未填 customerEmail，客户邮件跳过）"
                        : "，客户确认邮件 → " + customerEmail);
        return Result.success(msg);
    }
}
