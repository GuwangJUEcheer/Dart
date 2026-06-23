package hokumei.sys.brackeet.service.impl;

import hokumei.sys.brackeet.common.Result;
import hokumei.sys.brackeet.dto.CreateOrderRequest;
import hokumei.sys.brackeet.dto.OrderItemRequest;
import hokumei.sys.brackeet.entity.Order;
import hokumei.sys.brackeet.entity.OrderItem;
import hokumei.sys.brackeet.entity.Product;
import hokumei.sys.brackeet.mapper.OrderMapper;
import hokumei.sys.brackeet.mapper.ProductMapper;
import hokumei.sys.brackeet.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional
    public Result<Order> createOrder(CreateOrderRequest request, Long userId) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return Result.fail("购物车为空");
        }

        // 幂等检查：相同 key 直接返回已有订单，库存不重复扣减
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            Order existing = orderMapper.findByIdempotencyKey(request.getIdempotencyKey(), userId);
            if (existing != null) {
                existing.setItems(orderMapper.findItemsByOrderId(existing.getId()));
                return Result.success(existing);
            }
        }

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productMapper.findById(itemReq.getProductId());
            if (product == null || product.getStatus() == 0) {
                return Result.fail("商品 [" + itemReq.getProductId() + "] 不存在或已下架");
            }
            if (product.getStock() < itemReq.getQuantity()) {
                return Result.fail("商品 [" + product.getName() + "] 库存不足");
            }

            int affected = productMapper.decreaseStock(product.getId(), itemReq.getQuantity());
            if (affected == 0) {
                return Result.fail("商品 [" + product.getName() + "] 库存不足");
            }

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getImageUrl());
            item.setPrice(product.getPrice());
            item.setQuantity(itemReq.getQuantity());
            orderItems.add(item);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(total);
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setIdempotencyKey(request.getIdempotencyKey());
        orderMapper.insertOrder(order);

        orderItems.forEach(item -> item.setOrderId(order.getId()));
        orderMapper.insertOrderItems(orderItems);
        order.setItems(orderItems);

        return Result.success(order);
    }

    @Override
    public Result<Order> getOrder(Long id, Long userId) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        if (userId != null && !order.getUserId().equals(userId)) {
            return Result.fail("无权查看此订单");
        }
        order.setItems(orderMapper.findItemsByOrderId(id));
        return Result.success(order);
    }

    @Override
    public Result<List<Order>> getMyOrders(Long userId) {
        List<Order> orders = orderMapper.findByUserId(userId);
        orders.forEach(o -> o.setItems(orderMapper.findItemsByOrderId(o.getId())));
        return Result.success(orders);
    }

    @Override
    public Result<List<Order>> getAllOrders() {
        List<Order> orders = orderMapper.findAll();
        orders.forEach(o -> o.setItems(orderMapper.findItemsByOrderId(o.getId())));
        return Result.success(orders);
    }

    @Override
    public Result<Void> updateStatus(Long id, Integer status) {
        orderMapper.updateStatus(id, status);
        return Result.success(null);
    }
}
