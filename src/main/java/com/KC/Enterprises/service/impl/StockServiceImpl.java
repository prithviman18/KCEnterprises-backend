package com.KC.Enterprises.service.impl;

import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.StockRequest;
import com.KC.Enterprises.dto.StockResponse;
import com.KC.Enterprises.entity.Product;
import com.KC.Enterprises.entity.Stock;
import com.KC.Enterprises.exception.BusinessException;
import com.KC.Enterprises.exception.ResourceNotFoundException;
import com.KC.Enterprises.repository.ProductRepository;
import com.KC.Enterprises.repository.StockRepository;
import com.KC.Enterprises.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public StockResponse createStock(StockRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.getProductId()));

        if (stockRepository.findByProductId(request.getProductId()).isPresent()) {
            throw new BusinessException("Stock entry already exists for product id: " + request.getProductId());
        }

        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setQuantity(request.getQuantity() != null ? request.getQuantity() : 0);
        stock.setMinimumQuantity(request.getMinimumQuantity());

        return mapToResponse(stockRepository.save(stock));
    }

    @Override
    @Transactional(readOnly = true)
    public StockResponse getStockById(Long id) {
        return mapToResponse(stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found with id: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public StockResponse getStockByProductId(Long productId) {
        return mapToResponse(stockRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Stock not found for product id: " + productId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockResponse> getAllStock() {
        return stockRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockResponse> getLowStock() {
        return stockRepository.findAll()
                .stream()
                .filter(s -> s.getMinimumQuantity() != null && s.getQuantity() <= s.getMinimumQuantity())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StockResponse updateStock(Long id, StockRequest request) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found with id: " + id));

        if (request.getQuantity() != null) stock.setQuantity(request.getQuantity());
        if (request.getMinimumQuantity() != null) stock.setMinimumQuantity(request.getMinimumQuantity());

        return mapToResponse(stockRepository.save(stock));
    }

    @Override
    @Transactional
    public StockResponse adjustQuantity(Long productId, Integer delta) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Stock not found for product id: " + productId));

        int newQty = stock.getQuantity() + delta;
        if (newQty < 0) {
            throw new BusinessException("Insufficient stock. Available: " + stock.getQuantity());
        }
        stock.setQuantity(newQty);
        return mapToResponse(stockRepository.save(stock));
    }

    @Override
    @Transactional
    public DeleteResponse deleteStock(Long id) {
        if (!stockRepository.existsById(id)) {
            throw new ResourceNotFoundException("Stock not found with id: " + id);
        }
        stockRepository.deleteById(id);
        return DeleteResponse.builder()
                .success(true)
                .message("Stock deleted successfully")
                .deletedId(id)
                .build();
    }

    private StockResponse mapToResponse(Stock stock) {
        boolean isLow = stock.getMinimumQuantity() != null
                && stock.getQuantity() <= stock.getMinimumQuantity();
        return StockResponse.builder()
                .id(stock.getId())
                .productId(stock.getProduct().getId())
                .productName(stock.getProduct().getProductName())
                .category(stock.getProduct().getCategory())
                .brand(stock.getProduct().getBrand())
                .mrp(stock.getProduct().getMrp())
                .quantity(stock.getQuantity())
                .minimumQuantity(stock.getMinimumQuantity())
                .isLowStock(isLow)
                .updatedAt(stock.getUpdatedAt())
                .build();
    }
}