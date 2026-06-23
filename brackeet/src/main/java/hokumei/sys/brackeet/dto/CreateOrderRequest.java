package hokumei.sys.brackeet.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    private List<OrderItemRequest> items;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String idempotencyKey;
}
