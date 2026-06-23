package hokumei.sys.brackeet.service.impl;

import hokumei.sys.brackeet.common.Result;
import hokumei.sys.brackeet.dto.ProductCreateRequest;
import hokumei.sys.brackeet.dto.ProductStatusRequest;
import hokumei.sys.brackeet.dto.ProductUpdateRequest;
import hokumei.sys.brackeet.entity.Product;
import hokumei.sys.brackeet.mapper.ProductMapper;
import hokumei.sys.brackeet.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Result<List<Product>> listProducts(Integer status, String category) {
        List<Product> products = productMapper.findByFilter(status, category);
        return Result.success(products);
    }

    @Override
    public Result<Product> getProduct(Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            return Result.fail("商品不存在");
        }
        return Result.success(product);
    }

    @Override
    public Result<Product> createProduct(ProductCreateRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImageUrl(request.getImageUrl());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        productMapper.insert(product);
        return Result.success(product);
    }

    @Override
    public Result<Void> updateStatus(ProductStatusRequest request) {
        Product product = productMapper.findById(request.getId());
        if (product == null) {
            return Result.fail("商品不存在");
        }
        productMapper.updateStatus(request.getId(), request.getStatus());
        return Result.success(null);
    }

    @Override
    public Result<Product> updateProduct(ProductUpdateRequest request) {
        Product product = productMapper.findById(request.getId());
        if (product == null) {
            return Result.fail("商品不存在");
        }
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImageUrl(request.getImageUrl());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        productMapper.update(product);
        return Result.success(product);
    }
}
