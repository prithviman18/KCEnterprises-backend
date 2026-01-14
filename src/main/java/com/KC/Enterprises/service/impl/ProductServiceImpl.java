package com.KC.Enterprises.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.ProductRequest;
import com.KC.Enterprises.dto.ProductResponse;
import com.KC.Enterprises.entity.BookDetails;
import com.KC.Enterprises.entity.Product;
import com.KC.Enterprises.entity.Supplier;
import com.KC.Enterprises.exception.ResourceNotFoundException;
import com.KC.Enterprises.repository.BookDetailsRepository;
import com.KC.Enterprises.repository.ProductRepository;
import com.KC.Enterprises.repository.SupplierRepository;
import com.KC.Enterprises.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final BookDetailsRepository bookDetailsRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request){
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + request.getSupplierId()));
        BookDetails bookDetails = null;
        if(request.getBookDetailsId()!=null){
            bookDetails = bookDetailsRepository.findById(request.getBookDetailsId())
                    .orElseThrow(() -> new ResourceNotFoundException("Book details not found with id: " + request.getBookDetailsId()));
        }

        Product product = Product.builder()
        .productName(request.getProductName())
        .category(request.getCategory())
        .brand(request.getBrand())
        .unitOfMeasure(request.getUnitOfMeasure())
        .costPrice(request.getCostPrice())
        .mrp(request.getMrp())
        .supplier(supplier)
        .bookDetails(bookDetails)
        .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: "+ id));
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: "+ request.getSupplierId()));
        BookDetails bookDetails = null;
        if(request.getBookDetailsId()!=null){
            bookDetails = bookDetailsRepository.findById(request.getBookDetailsId())
                    .orElseThrow(() -> new ResourceNotFoundException("Book details not found with id: " + request.getBookDetailsId()));
        }
        product.setProductName(request.getProductName());
        product.setCategory(request.getCategory());
        product.setBrand(request.getBrand());
        product.setUnitOfMeasure(request.getUnitOfMeasure());
        product.setCostPrice(request.getCostPrice());
        product.setMrp(request.getMrp());
        product.setSupplier(supplier);
        product.setBookDetails(bookDetails);

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);

    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToResponse(product);
    }

    @Override
    public List<ProductResponse> getProductsBySupplier(Long supplierId) {
        // Validate supplier exists
        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier not found with id: " + supplierId);
        }

        return productRepository.findBySupplierId(supplierId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DeleteResponse deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);

        return DeleteResponse.builder()
                .success(true)
                .message("Product deleted successfully")
                .deletedId(id)
                .build();
    }


    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .category(product.getCategory())
                .brand(product.getBrand())
                .unitOfMeasure(product.getUnitOfMeasure())
                .costPrice(product.getCostPrice())
                .mrp(product.getMrp())
                .supplierId(product.getSupplier().getId())
                .supplierName(product.getSupplier().getSupplierName())
                .bookDetailsId(product.getBookDetails() != null ? product.getBookDetails().getId() : null)
                .bookTitle(product.getBookDetails() != null ? product.getBookDetails().getTitle() : null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    
}
