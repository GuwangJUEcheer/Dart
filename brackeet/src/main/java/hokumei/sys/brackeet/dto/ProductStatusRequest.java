package hokumei.sys.brackeet.dto;

import lombok.Data;

@Data
public class ProductStatusRequest {

    private Long id;
    /** 1=上架 0=下架 */
    private Integer status;
}
