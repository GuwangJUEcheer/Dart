package hokumei.sys.brackeet.controller;

import hokumei.sys.brackeet.common.Result;
import hokumei.sys.brackeet.dto.ProductCreateRequest;
import hokumei.sys.brackeet.dto.ProductStatusRequest;
import hokumei.sys.brackeet.dto.ProductUpdateRequest;
import hokumei.sys.brackeet.entity.Product;
import hokumei.sys.brackeet.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /** 公开接口：获取商品列表（默认只返回上架商品） */
    @GetMapping("/list")
    public Result<List<Product>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String category) {
        // 公开接口只允许查看上架商品，管理员通过 status 参数可查看全部
        return productService.listProducts(status, category);
    }

    /** 公开接口：获取商品详情 */
    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    /** 管理员接口：新增商品 */
    @PostMapping("/add")
    public Result<Product> add(@RequestBody ProductCreateRequest request) {
        return productService.createProduct(request);
    }

    /** 管理员接口：上架/下架商品 */
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestBody ProductStatusRequest request) {
        return productService.updateStatus(request);
    }

    /** 管理员接口：编辑商品信息（含库存） */
    @PutMapping("/update")
    public Result<Product> update(@RequestBody ProductUpdateRequest request) {
        return productService.updateProduct(request);
    }
}
