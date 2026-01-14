package com.KC.Enterprises.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.PurchaseOrderItemRequest;
import com.KC.Enterprises.dto.PurchaseOrderItemResponse;
import com.KC.Enterprises.dto.PurchaseOrderItemqtyRequest;
import com.KC.Enterprises.dto.PurchaseOrderRequest;
import com.KC.Enterprises.dto.PurchaseOrderResponse;
import com.KC.Enterprises.dto.PurchaseOrderSummaryResponse;
import com.KC.Enterprises.entity.Product;
import com.KC.Enterprises.entity.PurchaseOrder;
import com.KC.Enterprises.entity.PurchaseOrderItem;
import com.KC.Enterprises.entity.Supplier;
import com.KC.Enterprises.enums.PurchaseOrderStatus;
import com.KC.Enterprises.exception.BusinessException;
import com.KC.Enterprises.exception.ResourceNotFoundException;
import com.KC.Enterprises.repository.ProductRepository;
import com.KC.Enterprises.repository.PurchaseOrderItemRepository;
import com.KC.Enterprises.repository.PurchaseOrderRepository;
import com.KC.Enterprises.repository.SupplierRepository;
import com.KC.Enterprises.service.PurchaseOrderService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderServiceImpl implements PurchaseOrderService{

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Override
    @Transactional
    public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request){

        // Validate supplier exists
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with id: " + request.getSupplierId()));

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setOrderDate(request.getOrderDate());
        purchaseOrder.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        purchaseOrder.setStatus(request.getStatus() != null ? 
                request.getStatus() : PurchaseOrderStatus.CREATED);
        purchaseOrder.setRemarks(request.getRemarks());

        Double totalAmount = 0.0;
        for (PurchaseOrderItemRequest itemRequest : request.getItems()) {
            PurchaseOrderItem item = createPurchaseOrderItem(itemRequest, purchaseOrder);
            purchaseOrder.getItems().add(item);
            totalAmount += item.getTotalPrice();
        }
        
        purchaseOrder.setTotalAmount(totalAmount);

        PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);

        return mapToResponse(savedOrder);

    }

    private PurchaseOrderItem  createPurchaseOrderItem(PurchaseOrderItemRequest itemRequest,PurchaseOrder purchaseOrder){
        Product product = productRepository.findById(itemRequest.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Product not found with id: " + itemRequest.getProductId()
            ));

        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setPurchaseOrder(purchaseOrder);
        item.setProduct(product);
        item.setQuantity(itemRequest.getQuantity());
        item.setCostPrice(itemRequest.getCostPrice());
        item.setTotalPrice(itemRequest.getCostPrice() * itemRequest.getQuantity());

        return item;
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderResponse getPurchaseOrderById(Long id) {  

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purchase order not found with id: " + id));
        
        return mapToResponse(purchaseOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderSummaryResponse> getAllPurchaseOrders() {
      
        return purchaseOrderRepository.findAll()
                .stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());
    }

    private PurchaseOrderResponse mapToResponse(PurchaseOrder purchaseOrder) {
        List<PurchaseOrderItemResponse> itemResponses = purchaseOrder.getItems().stream()
                .map(this::mapToItemResponse)
                .collect(Collectors.toList());
        
        return PurchaseOrderResponse.builder()
                .id(purchaseOrder.getId())
                .supplierId(purchaseOrder.getSupplier().getId())
                .supplierName(purchaseOrder.getSupplier().getSupplierName())
                .orderDate(purchaseOrder.getOrderDate())
                .expectedDeliveryDate(purchaseOrder.getExpectedDeliveryDate())
                .status(purchaseOrder.getStatus())
                .totalAmount(purchaseOrder.getTotalAmount())
                .remarks(purchaseOrder.getRemarks())
                .createdAt(purchaseOrder.getCreatedAt())
                .items(itemResponses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderSummaryResponse> getPurchaseOrdersBySupplier(Long supplierId) {
       
        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier not found with id: " + supplierId);
        }
        
        return purchaseOrderRepository.findBySupplierId(supplierId)
                .stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderSummaryResponse> getPurchaseOrdersByStatus(String status) {
        
        try {
            PurchaseOrderStatus orderStatus = PurchaseOrderStatus.valueOf(status.toUpperCase());
            return purchaseOrderRepository.findByStatus(orderStatus)
                    .stream()
                    .map(this::mapToSummaryResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid status value: " + status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderSummaryResponse> getPurchaseOrdersByDateRange(
            LocalDate startDate, LocalDate endDate) {
        log.info("Fetching purchase orders between {} and {}", startDate, endDate);
        
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Start date cannot be after end date");
        }
        
        return purchaseOrderRepository.findByOrderDateBetween(startDate, endDate)
                .stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PurchaseOrderResponse updatePurchaseOrder(Long id, PurchaseOrderRequest request) {
        
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purchase order not found with id: " + id));
        
        // Check if order can be modified
        if (purchaseOrder.getStatus() == PurchaseOrderStatus.DELIVERED || 
            purchaseOrder.getStatus() == PurchaseOrderStatus.CANCELLED) {
            throw new BusinessException("Cannot modify a " + purchaseOrder.getStatus() + " order");
        }
        
        // Update basic information
        if (request.getSupplierId() != null && 
            !request.getSupplierId().equals(purchaseOrder.getSupplier().getId())) {
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Supplier not found with id: " + request.getSupplierId()));
            purchaseOrder.setSupplier(supplier);
        }
        
        if (request.getOrderDate() != null) {
            purchaseOrder.setOrderDate(request.getOrderDate());
        }
        
        if (request.getExpectedDeliveryDate() != null) {
            purchaseOrder.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        }
        
        if (request.getStatus() != null) {
            purchaseOrder.setStatus(request.getStatus());
        }
        
        if (request.getRemarks() != null) {
            purchaseOrder.setRemarks(request.getRemarks());
        }
        
        // Update items if provided
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            // First, remove existing items
            purchaseOrderItemRepository.deleteByPurchaseOrderId(id);
            
            // Clear the list
            purchaseOrder.getItems().clear();
            
            // Add new items
            Double totalAmount = 0.0;
            for (PurchaseOrderItemRequest itemRequest : request.getItems()) {
                PurchaseOrderItem item = createPurchaseOrderItem(itemRequest, purchaseOrder);
                purchaseOrder.getItems().add(item);
                totalAmount += item.getTotalPrice();
            }
            purchaseOrder.setTotalAmount(totalAmount);
        }
        
        PurchaseOrder updatedOrder = purchaseOrderRepository.save(purchaseOrder);
        return mapToResponse(updatedOrder);
    }

    @Override
    @Transactional
    public PurchaseOrderResponse updatePurchaseOrderStatus(Long id, String status) {
        log.info("Updating status of purchase order ID: {} to {}", id, status);
        
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purchase order not found with id: " + id));
        
        try {
            PurchaseOrderStatus newStatus = PurchaseOrderStatus.valueOf(status.toUpperCase());
            
            // Add any status transition validation here if needed
            purchaseOrder.setStatus(newStatus);
            
            PurchaseOrder updatedOrder = purchaseOrderRepository.save(purchaseOrder);
            log.info("Purchase order status updated to {}", newStatus);
            
            return mapToResponse(updatedOrder);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid status value: " + status);
        }
    }
    
    @Override
    @Transactional
    public DeleteResponse deletePurchaseOrder(Long id) {
        
        if (!purchaseOrderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Purchase order not found with id: " + id);
        }
        
        purchaseOrderRepository.deleteById(id);
        
        return DeleteResponse.builder()
                .success(true)
                .message("Purchase order deleted successfully")
                .deletedId(id)
                .build();
    }

    @Override
    @Transactional
    public PurchaseOrderResponse addItemToPurchaseOrder(Long orderId, 
                                                       PurchaseOrderItemRequest itemRequest) {
        log.info("Adding item to purchase order ID: {}", orderId);
        
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purchase order not found with id: " + orderId));
        
        // Check if order can be modified
        if (purchaseOrder.getStatus() == PurchaseOrderStatus.DELIVERED || 
            purchaseOrder.getStatus() == PurchaseOrderStatus.CANCELLED) {
            throw new BusinessException("Cannot modify a " + purchaseOrder.getStatus() + " order");
        }
        
        // Check if product already exists in order
        boolean productExists = purchaseOrder.getItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(itemRequest.getProductId()));
        
        if (productExists) {
            throw new BusinessException("Product already exists in this purchase order. " +
                    "Consider updating the quantity instead.");
        }
        
        // Create and add new item
        PurchaseOrderItem item = createPurchaseOrderItem(itemRequest, purchaseOrder);
        purchaseOrder.getItems().add(item);
        
        // Recalculate total amount
        Double totalAmount = purchaseOrder.getItems().stream()
                .mapToDouble(PurchaseOrderItem::getTotalPrice)
                .sum();
        purchaseOrder.setTotalAmount(totalAmount);
        
        PurchaseOrder updatedOrder = purchaseOrderRepository.save(purchaseOrder);
        log.info("Item added to purchase order ID: {}", orderId);
        
        return mapToResponse(updatedOrder);
    }

    @Override
    @Transactional
    public PurchaseOrderResponse removeItemFromPurchaseOrder(Long orderId, Long itemId) {
        log.info("Removing item ID: {} from purchase order ID: {}", itemId, orderId);
        
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purchase order not found with id: " + orderId));
        
        // Check if order can be modified
        if (purchaseOrder.getStatus() == PurchaseOrderStatus.DELIVERED || 
            purchaseOrder.getStatus() == PurchaseOrderStatus.CANCELLED) {
            throw new BusinessException("Cannot modify a " + purchaseOrder.getStatus() + " order");
        }
        
        // Find the item
        PurchaseOrderItem itemToRemove = purchaseOrder.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Item not found with id: " + itemId + " in purchase order: " + orderId));
        
        // Remove the item
        purchaseOrder.getItems().remove(itemToRemove);
        
        // Delete from database
        purchaseOrderItemRepository.delete(itemToRemove);
        
        // Recalculate total amount
        Double totalAmount = purchaseOrder.getItems().stream()
                .mapToDouble(PurchaseOrderItem::getTotalPrice)
                .sum();
        purchaseOrder.setTotalAmount(totalAmount);
        
        PurchaseOrder updatedOrder = purchaseOrderRepository.save(purchaseOrder);
        log.info("Item removed from purchase order ID: {}", orderId);
        
        return mapToResponse(updatedOrder);
    }

    // In PurchaseOrderServiceImpl.java:
@Override
@Transactional
public PurchaseOrderResponse updateItemQuantity(Long orderId, Long itemId, PurchaseOrderItemqtyRequest request) {
    log.info("Updating quantity for item ID: {} in purchase order ID: {}", itemId, orderId);
    
    // Fetch purchase order with items
    PurchaseOrder purchaseOrder = purchaseOrderRepository.findByIdWithItems(orderId)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Purchase order not found with id: " + orderId));
    
    // Check if order can be modified
    if (purchaseOrder.getStatus() == PurchaseOrderStatus.DELIVERED || 
        purchaseOrder.getStatus() == PurchaseOrderStatus.CANCELLED) {
        throw new BusinessException("Cannot modify a " + purchaseOrder.getStatus() + " order");
    }
    
    // Find the specific item
    PurchaseOrderItem itemToUpdate = purchaseOrder.getItems().stream()
            .filter(item -> item.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Item not found with id: " + itemId + " in purchase order: " + orderId));
    
    // Update quantity
    itemToUpdate.setQuantity(request.getQuantity());
    
    // Update cost price if provided
    if (request.getCostPrice() != null) {
        itemToUpdate.setCostPrice(request.getCostPrice());
    }
    
    // Recalculate total price for this item
    itemToUpdate.setTotalPrice(itemToUpdate.getCostPrice() * itemToUpdate.getQuantity());
    
    // Recalculate total amount for the entire order
    Double totalAmount = purchaseOrder.getItems().stream()
            .mapToDouble(PurchaseOrderItem::getTotalPrice)
            .sum();
    purchaseOrder.setTotalAmount(totalAmount);
    
    // Save the updated order (cascade will update the item)
    PurchaseOrder updatedOrder = purchaseOrderRepository.save(purchaseOrder);
    log.info("Item quantity updated. New quantity: {}", request.getQuantity());
    
    return mapToResponse(updatedOrder);
}


    private PurchaseOrderItemResponse mapToItemResponse(PurchaseOrderItem item) {
        return PurchaseOrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getProductName())
                .productCategory(item.getProduct().getCategory())
                .quantity(item.getQuantity())
                .costPrice(item.getCostPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    private PurchaseOrderSummaryResponse mapToSummaryResponse(PurchaseOrder purchaseOrder) {
        return PurchaseOrderSummaryResponse.builder()
                .id(purchaseOrder.getId())
                .supplierId(purchaseOrder.getSupplier().getId())
                .supplierName(purchaseOrder.getSupplier().getSupplierName())
                .orderDate(purchaseOrder.getOrderDate())
                .expectedDeliveryDate(purchaseOrder.getExpectedDeliveryDate())
                .status(purchaseOrder.getStatus())
                .totalAmount(purchaseOrder.getTotalAmount())
                .totalItems(purchaseOrder.getItems().size())
                .createdAt(purchaseOrder.getCreatedAt())
                .build();
    }

}
