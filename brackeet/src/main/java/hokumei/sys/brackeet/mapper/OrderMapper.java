package hokumei.sys.brackeet.mapper;

import hokumei.sys.brackeet.entity.Order;
import hokumei.sys.brackeet.entity.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO \"order\"(user_id, total_amount, status, receiver_name, receiver_phone, receiver_address, idempotency_key) " +
            "VALUES(#{userId}, #{totalAmount}, 0, #{receiverName}, #{receiverPhone}, #{receiverAddress}, #{idempotencyKey})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrder(Order order);

    @Select("SELECT * FROM \"order\" WHERE idempotency_key = #{key} AND user_id = #{userId}")
    Order findByIdempotencyKey(@Param("key") String key, @Param("userId") Long userId);

    @Insert("<script>" +
            "INSERT INTO order_item(order_id, product_id, product_name, product_image, price, quantity) VALUES " +
            "<foreach collection='items' item='item' separator=','>" +
            "(#{item.orderId}, #{item.productId}, #{item.productName}, #{item.productImage}, #{item.price}, #{item.quantity})" +
            "</foreach>" +
            "</script>")
    int insertOrderItems(@Param("items") List<OrderItem> items);

    @Select("SELECT * FROM \"order\" WHERE id = #{id}")
    Order findById(Long id);

    @Select("SELECT * FROM \"order\" WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Order> findByUserId(Long userId);

    @Select("SELECT * FROM \"order\" ORDER BY create_time DESC")
    List<Order> findAll();

    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> findItemsByOrderId(Long orderId);

    @Update("UPDATE \"order\" SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE \"order\" SET stripe_payment_intent_id = #{intentId} WHERE id = #{id}")
    int updatePaymentIntentId(@Param("id") Long id, @Param("intentId") String intentId);
}
