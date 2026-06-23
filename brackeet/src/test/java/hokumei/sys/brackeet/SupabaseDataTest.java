package hokumei.sys.brackeet;

import hokumei.sys.brackeet.mapper.FortuneMapper;
import hokumei.sys.brackeet.mapper.ProductMapper;
import hokumei.sys.brackeet.mapper.UserMapper;
import hokumei.sys.brackeet.entity.User;
import hokumei.sys.brackeet.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SupabaseDataTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private FortuneMapper fortuneMapper;

    @Test
    void adminUserExists() {
        User admin = userMapper.findByUsername("admin");
        assertNotNull(admin, "admin 用户不存在，users 表数据未插入");
        assertEquals("admin", admin.getRole(), "admin 角色错误");
        System.out.println("✓ admin 用户: id=" + admin.getId() + ", role=" + admin.getRole());
    }

    @Test
    void productsInserted() {
        List<Product> products = productMapper.findAll();
        assertFalse(products.isEmpty(), "product 表为空，商品数据未插入");
        assertEquals(12, products.size(), "商品数量应为 12 条");
        System.out.println("✓ 商品数量: " + products.size());
        products.forEach(p -> System.out.println("  - [" + p.getId() + "] " + p.getName()));
    }

    @Test
    void fortuneLuckyInserted() {
        Map<String, Object> lucky = fortuneMapper.findLucky("牡羊座");
        assertNotNull(lucky, "fortune_lucky 表为空，占い数据未插入");
        assertEquals("カーネリアン", lucky.get("lucky_stone"));
        System.out.println("✓ 牡羊座 lucky_stone: " + lucky.get("lucky_stone"));
    }

    @Test
    void fortuneMessageInserted() {
        String msg = fortuneMapper.findMessage("牡羊座", "love");
        assertNotNull(msg, "fortune_message 表为空，メッセージ数据未插入");
        assertFalse(msg.isBlank());
        System.out.println("✓ 牡羊座 love message: " + msg.substring(0, 20) + "...");
    }
}
