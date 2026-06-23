package hokumei.sys.brackeet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreateRequest {

    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Integer stock;
    private String category;
}
