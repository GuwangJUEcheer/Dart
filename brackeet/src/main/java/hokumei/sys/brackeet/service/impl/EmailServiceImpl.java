package hokumei.sys.brackeet.service.impl;

import hokumei.sys.brackeet.entity.Order;
import hokumei.sys.brackeet.entity.OrderItem;
import hokumei.sys.brackeet.entity.User;
import hokumei.sys.brackeet.service.EmailService;
import hokumei.sys.brackeet.service.GmailEmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private GmailEmailSender gmailEmailSender;

    @Value("${gmail.sender-email}")
    private String senderEmail;

    @Value("${merchant.email}")
    private String merchantEmail;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Async
    @Override
    public void sendMerchantOrderNotification(Order order, User customer) {
        try {
            gmailEmailSender.send(
                    merchantEmail,
                    "[Brackeet] 新订单通知 - 订单 #" + order.getId(),
                    buildMerchantHtml(order, customer));
        } catch (Exception e) {
            System.err.println("[EmailService] 商家邮件发送失败: " + e.getMessage());
        }
    }

    @Async
    @Override
    public void sendCustomerOrderConfirmation(Order order, User customer) {
        if (customer == null || customer.getEmail() == null || customer.getEmail().isBlank()) return;
        try {
            gmailEmailSender.send(
                    customer.getEmail(),
                    "【Brackeet】ご注文確認 - 注文番号 #" + order.getId(),
                    buildCustomerHtml(order, customer));
        } catch (Exception e) {
            System.err.println("[EmailService] 客户邮件发送失败: " + e.getMessage());
        }
    }

    private String buildMerchantHtml(Order order, User customer) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family:sans-serif;max-width:600px;margin:auto;color:#333'>")
          .append("<h2 style='color:#c0392b'>🛍️ 新订单通知</h2>")
          .append("<p><b>订单编号:</b> #").append(order.getId()).append("</p>")
          .append("<p><b>下单时间:</b> ").append(order.getCreateTime() != null ? order.getCreateTime().format(FMT) : "-").append("</p>")
          .append("<p><b>客户:</b> ").append(customer != null ? esc(customer.getNickname()) : "未知").append("</p>")
          .append("<hr>")
          .append("<h3>商品明细</h3>")
          .append("<table style='width:100%;border-collapse:collapse'>")
          .append("<tr style='background:#f5f5f5'><th style='text-align:left;padding:8px'>商品</th><th style='padding:8px'>数量</th><th style='padding:8px'>单价</th><th style='padding:8px'>小计</th></tr>");

        List<OrderItem> items = order.getItems();
        if (items != null) {
            for (OrderItem item : items) {
                BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                sb.append("<tr style='border-bottom:1px solid #eee'>")
                  .append("<td style='padding:8px'>").append(esc(item.getProductName())).append("</td>")
                  .append("<td style='padding:8px;text-align:center'>").append(item.getQuantity()).append("</td>")
                  .append("<td style='padding:8px;text-align:right'>¥").append(item.getPrice().toPlainString()).append("</td>")
                  .append("<td style='padding:8px;text-align:right'>¥").append(subtotal.toPlainString()).append("</td>")
                  .append("</tr>");
            }
        }

        sb.append("</table>")
          .append("<p style='text-align:right;font-size:1.1em'><b>订单总额: ¥").append(order.getTotalAmount().toPlainString()).append("</b></p>")
          .append("<hr>")
          .append("<h3>收货信息</h3>")
          .append("<p><b>姓名:</b> ").append(esc(order.getReceiverName())).append("</p>")
          .append("<p><b>电话:</b> ").append(esc(order.getReceiverPhone())).append("</p>")
          .append("<p><b>地址:</b> ").append(esc(order.getReceiverAddress())).append("</p>")
          .append("</div>");
        return sb.toString();
    }

    private String buildCustomerHtml(Order order, User customer) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family:sans-serif;max-width:600px;margin:auto;color:#333'>")
          .append("<h2 style='color:#8e44ad'>✨ ご注文ありがとうございます</h2>")
          .append("<p>").append(esc(customer.getNickname())).append(" 様、ご注文を承りました。</p>")
          .append("<p><b>注文番号:</b> #").append(order.getId()).append("</p>")
          .append("<p><b>注文日時:</b> ").append(order.getCreateTime() != null ? order.getCreateTime().format(FMT) : "-").append("</p>")
          .append("<hr>")
          .append("<h3>ご注文内容</h3>")
          .append("<table style='width:100%;border-collapse:collapse'>")
          .append("<tr style='background:#f5f5f5'><th style='text-align:left;padding:8px'>商品</th><th style='padding:8px'>数量</th><th style='padding:8px'>単価</th><th style='padding:8px'>小計</th></tr>");

        List<OrderItem> items = order.getItems();
        if (items != null) {
            for (OrderItem item : items) {
                BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                sb.append("<tr style='border-bottom:1px solid #eee'>")
                  .append("<td style='padding:8px'>").append(esc(item.getProductName())).append("</td>")
                  .append("<td style='padding:8px;text-align:center'>").append(item.getQuantity()).append("</td>")
                  .append("<td style='padding:8px;text-align:right'>¥").append(item.getPrice().toPlainString()).append("</td>")
                  .append("<td style='padding:8px;text-align:right'>¥").append(subtotal.toPlainString()).append("</td>")
                  .append("</tr>");
            }
        }

        sb.append("</table>")
          .append("<p style='text-align:right;font-size:1.1em'><b>合計金額: ¥").append(order.getTotalAmount().toPlainString()).append("</b></p>")
          .append("<hr>")
          .append("<h3>お届け先</h3>")
          .append("<p>").append(esc(order.getReceiverName())).append(" 様</p>")
          .append("<p>").append(esc(order.getReceiverPhone())).append("</p>")
          .append("<p>").append(esc(order.getReceiverAddress())).append("</p>")
          .append("<hr>")
          .append("<p style='color:#888;font-size:0.9em'>ご不明な点がございましたらお気軽にお問い合わせください。<br>Brackeet</p>")
          .append("</div>");
        return sb.toString();
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
