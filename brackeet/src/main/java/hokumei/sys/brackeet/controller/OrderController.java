package hokumei.sys.brackeet.controller;

import hokumei.sys.brackeet.common.Result;
import hokumei.sys.brackeet.dto.CreateOrderRequest;
import hokumei.sys.brackeet.entity.Order;
import hokumei.sys.brackeet.entity.User;
import hokumei.sys.brackeet.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /** 创建订单 */
    @PostMapping("/create")
    public Result<Order> create(@RequestBody CreateOrderRequest request, HttpServletRequest httpRequest) {
        User user = (User) httpRequest.getAttribute("currentUser");
        return orderService.createOrder(request, user.getId());
    }

    /** 获取当前用户的订单列表 */
    @GetMapping("/my")
    public Result<List<Order>> myOrders(HttpServletRequest httpRequest) {
        User user = (User) httpRequest.getAttribute("currentUser");
        return orderService.getMyOrders(user.getId());
    }

    /** 获取订单详情 */
    @GetMapping("/{id}")
    public Result<Order> detail(@PathVariable Long id, HttpServletRequest httpRequest) {
        User user = (User) httpRequest.getAttribute("currentUser");
        return orderService.getOrder(id, user.getId());
    }

    /** 管理员接口：获取所有订单 */
    @GetMapping("/all")
    public Result<List<Order>> allOrders() {
        return orderService.getAllOrders();
    }
}
