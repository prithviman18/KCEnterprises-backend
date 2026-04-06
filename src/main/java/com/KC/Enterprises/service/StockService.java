package com.KC.Enterprises.service;

import com.KC.Enterprises.dto.StockResponse;
import com.KC.Enterprises.dto.StockRequest;
import com.KC.Enterprises.dto.DeleteResponse;
import java.util.List;

public interface StockService {
    StockResponse createStock(StockRequest request);
    StockResponse getStockById(Long id);
    StockResponse getStockByProductId(Long productId);
    List<StockResponse> getAllStock();
    List<StockResponse> getLowStock();
    StockResponse updateStock(Long id, StockRequest request);
    StockResponse adjustQuantity(Long productId, Integer delta);
    DeleteResponse deleteStock(Long id);
}