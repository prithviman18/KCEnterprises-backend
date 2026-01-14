package com.KC.Enterprises.service;

import java.util.List;

import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.ProductRequest;
import com.KC.Enterprises.dto.ProductResponse;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse getProductById(Long id);
    List<ProductResponse> getAllProducts();
    List<ProductResponse> getProductsBySupplier(Long supplierId);
    List<ProductResponse> getProductsByCategory(String category);
    ProductResponse updateProduct(Long id, ProductRequest request);
    DeleteResponse deleteProduct(Long id);
}
