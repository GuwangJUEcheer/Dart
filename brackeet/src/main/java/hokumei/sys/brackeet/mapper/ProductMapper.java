package hokumei.sys.brackeet.mapper;

import hokumei.sys.brackeet.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM product WHERE status = 1 ORDER BY create_time DESC")
    List<Product> findAllActive();

    @Select("SELECT * FROM product ORDER BY create_time DESC")
    List<Product> findAll();

    @Select("SELECT * FROM product WHERE id = #{id}")
    Product findById(Long id);

    @Select("<script>" +
            "SELECT * FROM product WHERE 1=1" +
            "<if test='status != null'> AND status = #{status}</if>" +
            "<if test='category != null and category != \"\"'> AND category = #{category}</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<Product> findByFilter(@Param("status") Integer status, @Param("category") String category);

    @Insert("INSERT INTO product(name, description, price, image_url, stock, status, category) " +
            "VALUES(#{name}, #{description}, #{price}, #{imageUrl}, #{stock}, 1, #{category})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    @Update("UPDATE product SET name=#{name}, description=#{description}, price=#{price}, " +
            "image_url=#{imageUrl}, stock=#{stock}, category=#{category} WHERE id=#{id}")
    int update(Product product);

    @Update("UPDATE product SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE product SET stock = stock - #{quantity} WHERE id = #{id} AND stock >= #{quantity}")
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);
}
