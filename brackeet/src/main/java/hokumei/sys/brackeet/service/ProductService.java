package hokumei.sys.brackeet.service;

import hokumei.sys.brackeet.common.Result;
import hokumei.sys.brackeet.dto.ProductCreateRequest;
import hokumei.sys.brackeet.dto.ProductStatusRequest;
import hokumei.sys.brackeet.dto.ProductUpdateRequest;
import hokumei.sys.brackeet.entity.Product;

import java.util.List;

public interface ProductService {

    Result<List<Product>> listProducts(Integer status, String category);

    Result<Product> getProduct(Long id);

    Result<Product> createProduct(ProductCreateRequest request);

    Result<Void> updateStatus(ProductStatusRequest request);

    Result<Product> updateProduct(ProductUpdateRequest request);
}
