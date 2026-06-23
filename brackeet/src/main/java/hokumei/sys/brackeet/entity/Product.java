package hokumei.sys.brackeet.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Integer stock;
    /** 1=上架 0=下架 */
    private Integer status;
    private String category;
    private LocalDateTime createTime;
}
