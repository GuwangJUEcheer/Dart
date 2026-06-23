package hokumei.sys.brackeet.service;

import hokumei.sys.brackeet.common.Result;
import hokumei.sys.brackeet.dto.CreateOrderRequest;
import hokumei.sys.brackeet.entity.Order;

import java.util.List;

public interface OrderService {

    Result<Order> createOrder(CreateOrderRequest request, Long userId);

    Result<Order> getOrder(Long id, Long userId);

    Result<List<Order>> getMyOrders(Long userId);

    Result<List<Order>> getAllOrders();

    Result<Void> updateStatus(Long id, Integer status);
}
