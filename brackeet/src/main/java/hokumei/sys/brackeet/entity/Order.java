package hokumei.sys.brackeet.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {

    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    /** 0=待支付 1=已支付 2=已发货 3=已完成 4=已取消 */
    private Integer status;
    private String stripePaymentIntentId;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String idempotencyKey;
    private LocalDateTime createTime;

    private List<OrderItem> items;
}
