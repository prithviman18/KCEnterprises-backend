package com.KC.Enterprises.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.KC.Enterprises.dto.DeleteResponse;
import com.KC.Enterprises.dto.DiscountCalculationResponse;
import com.KC.Enterprises.dto.SchoolOrderStatisticsResponse;
import com.KC.Enterprises.dto.SchoolPurchaseOrderItemRequest;
import com.KC.Enterprises.dto.SchoolPurchaseOrderItemResponse;
import com.KC.Enterprises.dto.SchoolPurchaseOrderRequest;
import com.KC.Enterprises.dto.SchoolPurchaseOrderResponse;
import com.KC.Enterprises.dto.SchoolPurchaseOrderSummaryResponse;
import com.KC.Enterprises.entity.Product;
import com.KC.Enterprises.entity.School;
import com.KC.Enterprises.entity.SchoolProductPrice;
import com.KC.Enterprises.entity.SchoolPurchaseOrder;
import com.KC.Enterprises.entity.SchoolPurchaseOrderItem;
import com.KC.Enterprises.enums.SchoolOrderStatus;
import com.KC.Enterprises.exception.BusinessException;
import com.KC.Enterprises.exception.ResourceNotFoundException;
import com.KC.Enterprises.repository.ProductRepository;
import com.KC.Enterprises.repository.SchoolProductPriceRepository;
import com.KC.Enterprises.repository.SchoolPurchaseOrderItemRepository;
import com.KC.Enterprises.repository.SchoolPurchaseOrderRepository;
import com.KC.Enterprises.repository.SchoolRepository;
import com.KC.Enterprises.service.SchoolPurchaseOrderService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolPurchaseOrderServiceImpl implements SchoolPurchaseOrderService{

    private final SchoolRepository schoolRepository;
    private final SchoolPurchaseOrderRepository schoolPurchaseOrderRepository;
    private final SchoolPurchaseOrderItemRepository schoolPurchaseOrderItemRepository;
    private final ProductRepository productRepository;
    private final SchoolProductPriceRepository schoolProductPriceRepository;

    @Transactional
    @Override
    public SchoolPurchaseOrderResponse createPurchaseOrder(SchoolPurchaseOrderRequest request){
        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School not found with id: " + request.getSchoolId()));
        
        SchoolPurchaseOrder purchaseOrder = new SchoolPurchaseOrder();
        purchaseOrder.setSchool(school);
        purchaseOrder.setOrderDate(request.getOrderDate() != null ? 
        request.getOrderDate() : LocalDateTime.now());
        purchaseOrder.setDeliveryDate(request.getExpectedDeliveryDate());
        purchaseOrder.setStatus(request.getStatus() != null ? 
                request.getStatus() : SchoolOrderStatus.CREATED);
        purchaseOrder.setRemarks(request.getRemarks());

        SchoolPurchaseOrder savedOrder = schoolPurchaseOrderRepository.save(purchaseOrder);

        Double totalAmount = 0.0;
        Double totalDiscount = 0.0;

        for (SchoolPurchaseOrderItemRequest itemRequest : request.getItems()) {
            SchoolPurchaseOrderItem item = createSchoolPurchaseOrderItem(itemRequest, savedOrder);
            schoolPurchaseOrderItemRepository.save(item);
            totalAmount += item.getTotalAmount();
            totalDiscount += item.getDiscountAmount() != null ? 
                    item.getDiscountAmount() * item.getQuantity() : 0.0;
        }

        savedOrder.setTotalAmount(Math.round(totalAmount * 100.0) / 100.0);
        savedOrder.setTotalDiscount(Math.round(totalDiscount * 100.0) / 100.0);

        SchoolPurchaseOrder finalOrder = schoolPurchaseOrderRepository.save(savedOrder);
        log.info("Created school purchase order with ID: {}", finalOrder.getId());

        List<SchoolPurchaseOrderItem> items = schoolPurchaseOrderItemRepository
                .findByPurchaseOrderId(finalOrder.getId());
        
        return mapToResponse(finalOrder, items);
    }

    private SchoolPurchaseOrderItem createSchoolPurchaseOrderItem(
        SchoolPurchaseOrderItemRequest itemRequest, 
        SchoolPurchaseOrder purchaseOrder) {
    
    // Validate product exists
    Product product = productRepository.findById(itemRequest.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Product not found with id: " + itemRequest.getProductId()));

    // Check if school has negotiated price
    Optional<SchoolProductPrice> schoolProductPrice = schoolProductPriceRepository
            .findBySchoolIdAndProductId(purchaseOrder.getSchool().getId(), product.getId());
    
    // Create item
    SchoolPurchaseOrderItem item = new SchoolPurchaseOrderItem();
    item.setPurchaseOrder(purchaseOrder);
    item.setProduct(product);
    item.setQuantity(itemRequest.getQuantity());
    
    // Determine final selling price
    Double finalSellingPrice = determineFinalSellingPrice(
            itemRequest, product, schoolProductPrice.orElse(null));
    item.setFinalSellingPrice(finalSellingPrice);
    
    // Calculate amounts
    item.calculateAmounts();
    item.validateDiscount();
    
    return item;
}

private Double determineFinalSellingPrice(
        SchoolPurchaseOrderItemRequest itemRequest,
        Product product,
        SchoolProductPrice schoolProductPrice) {
    
    // Priority 1: Use explicitly provided final selling price
    if (itemRequest.getFinalSellingPrice() != null) {
        return itemRequest.getFinalSellingPrice();
    }
    
    // Priority 2: Calculate from discount percentage
    if (itemRequest.getDiscountPercentage() != null) {
        double discountAmount = product.getMrp() * (itemRequest.getDiscountPercentage() / 100);
        return Math.round((product.getMrp() - discountAmount) * 100.0) / 100.0;
    }
    
    // Priority 3: Use school's negotiated price
    if (schoolProductPrice != null && schoolProductPrice.getNegotiatedSellingPrice() != null) {
        return schoolProductPrice.getNegotiatedSellingPrice();
    }
    
    // Priority 4: Use product MRP (no discount)
    return product.getMrp();
}

    @Override
    @Transactional(readOnly = true)
    public SchoolPurchaseOrderResponse getPurchaseOrderById(Long id) {
        log.info("Fetching school purchase order with ID: {}", id);
        
        // Get the order
        SchoolPurchaseOrder purchaseOrder = schoolPurchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School purchase order not found with id: " + id));
        
        // Get items separately
        List<SchoolPurchaseOrderItem> items = schoolPurchaseOrderItemRepository.findByPurchaseOrderId(id);
        
        return mapToResponse(purchaseOrder, items);
    }


    @Override
    @Transactional(readOnly = true)
    public List<SchoolPurchaseOrderSummaryResponse> getAllPurchaseOrders() {
        log.info("Fetching all school purchase orders");
        
        List<SchoolPurchaseOrder> orders = schoolPurchaseOrderRepository.findAll();
        
        return orders.stream()
                .map(order -> {
                    // Get item count for each order
                    List<SchoolPurchaseOrderItem> items = schoolPurchaseOrderItemRepository
                            .findByPurchaseOrderId(order.getId());
                    return mapToSummaryResponse(order, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SchoolPurchaseOrderSummaryResponse> getPurchaseOrdersBySchool(Long schoolId) {
        log.info("Fetching school purchase orders for school ID: {}", schoolId);
        
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School not found with id: " + schoolId);
        }
        
        List<SchoolPurchaseOrder> orders = schoolPurchaseOrderRepository.findBySchoolId(schoolId);
        
        return orders.stream()
                .map(order -> {
                    List<SchoolPurchaseOrderItem> items = schoolPurchaseOrderItemRepository
                            .findByPurchaseOrderId(order.getId());
                    return mapToSummaryResponse(order, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SchoolPurchaseOrderSummaryResponse> getPurchaseOrdersByStatus(String status) {
        log.info("Fetching school purchase orders by status: {}", status);
        
        try {
            SchoolOrderStatus orderStatus = SchoolOrderStatus.valueOf(status.toUpperCase());
            List<SchoolPurchaseOrder> orders = schoolPurchaseOrderRepository.findByStatus(orderStatus);
            
            return orders.stream()
                    .map(order -> {
                        List<SchoolPurchaseOrderItem> items = schoolPurchaseOrderItemRepository
                                .findByPurchaseOrderId(order.getId());
                        return mapToSummaryResponse(order, items);
                    })
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid status value: " + status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SchoolPurchaseOrderSummaryResponse> getPurchaseOrdersByDateRange(
            LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching school purchase orders between {} and {}", startDate, endDate);
        
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Start date cannot be after end date");
        }
        
        List<SchoolPurchaseOrder> orders = schoolPurchaseOrderRepository
                .findByOrderDateBetween(startDate, endDate);
        
        return orders.stream()
                .map(order -> {
                    List<SchoolPurchaseOrderItem> items = schoolPurchaseOrderItemRepository
                            .findByPurchaseOrderId(order.getId());
                    return mapToSummaryResponse(order, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SchoolPurchaseOrderResponse updatePurchaseOrder(Long id, SchoolPurchaseOrderRequest request) {
        log.info("Updating school purchase order with ID: {}", id);
        
        SchoolPurchaseOrder purchaseOrder = schoolPurchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School purchase order not found with id: " + id));
        
        // Check if order can be modified
        if (purchaseOrder.getStatus() == SchoolOrderStatus.DELIVERED || 
            purchaseOrder.getStatus() == SchoolOrderStatus.CANCELLED) {
            throw new BusinessException("Cannot modify a " + purchaseOrder.getStatus() + " order");
        }
        
        // Update school if changed
        if (request.getSchoolId() != null && 
            !request.getSchoolId().equals(purchaseOrder.getSchool().getId())) {
            School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "School not found with id: " + request.getSchoolId()));
            purchaseOrder.setSchool(school);
        }
        
        // Update basic information
        if (request.getOrderDate() != null) {
            purchaseOrder.setOrderDate(request.getOrderDate());
        }
        
        if (request.getExpectedDeliveryDate() != null) {
            purchaseOrder.setDeliveryDate(request.getExpectedDeliveryDate());
        }
        
        if (request.getStatus() != null) {
            purchaseOrder.setStatus(request.getStatus());
        }
        
        if (request.getRemarks() != null) {
            purchaseOrder.setRemarks(request.getRemarks());
        }
        
        // Update items if provided
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            // Remove existing items
            schoolPurchaseOrderItemRepository.deleteByPurchaseOrderId(id);
            
            // Create new items
            Double totalAmount = 0.0;
            Double totalDiscount = 0.0;
            
            for (SchoolPurchaseOrderItemRequest itemRequest : request.getItems()) {
                SchoolPurchaseOrderItem item = createSchoolPurchaseOrderItem(itemRequest, purchaseOrder);
                schoolPurchaseOrderItemRepository.save(item);
                totalAmount += item.getTotalAmount();
                totalDiscount += item.getDiscountAmount() != null ? 
                        item.getDiscountAmount() * item.getQuantity() : 0.0;
            }
            
            purchaseOrder.setTotalAmount(Math.round(totalAmount * 100.0) / 100.0);
            purchaseOrder.setTotalDiscount(Math.round(totalDiscount * 100.0) / 100.0);
        }
        
        SchoolPurchaseOrder updatedOrder = schoolPurchaseOrderRepository.save(purchaseOrder);
        log.info("Updated school purchase order with ID: {}", id);
        
        // Get updated items for response
        List<SchoolPurchaseOrderItem> items = schoolPurchaseOrderItemRepository
                .findByPurchaseOrderId(updatedOrder.getId());
        
        return mapToResponse(updatedOrder, items);
    }

    @Override
    @Transactional
    public SchoolPurchaseOrderResponse updatePurchaseOrderStatus(Long id, String status) {
        log.info("Updating status of school purchase order ID: {} to {}", id, status);
        
        SchoolPurchaseOrder purchaseOrder = schoolPurchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School purchase order not found with id: " + id));
        
        try {
            SchoolOrderStatus newStatus = SchoolOrderStatus.valueOf(status.toUpperCase());
            
            // Handle status-specific logic
            if (newStatus == SchoolOrderStatus.DELIVERED) {
                purchaseOrder.setDeliveryDate(LocalDateTime.now());
            }
            
            purchaseOrder.setStatus(newStatus);
            
            SchoolPurchaseOrder updatedOrder = schoolPurchaseOrderRepository.save(purchaseOrder);
            log.info("School purchase order status updated to {}", newStatus);
            
            // Get items for response
            List<SchoolPurchaseOrderItem> items = schoolPurchaseOrderItemRepository
                    .findByPurchaseOrderId(updatedOrder.getId());
            
            return mapToResponse(updatedOrder, items);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid status value: " + status);
        }
    }

    @Override
    @Transactional
    public DeleteResponse deletePurchaseOrder(Long id) {
        log.info("Deleting school purchase order with ID: {}", id);
        
        if (!schoolPurchaseOrderRepository.existsById(id)) {
            throw new ResourceNotFoundException("School purchase order not found with id: " + id);
        }
        
        // First delete all items
        schoolPurchaseOrderItemRepository.deleteByPurchaseOrderId(id);
        
        // Then delete the order
        schoolPurchaseOrderRepository.deleteById(id);
        
        log.info("Deleted school purchase order with ID: {}", id);
        return DeleteResponse.builder()
                .success(true)
                .message("School purchase order deleted successfully")
                .deletedId(id)
                .build();
    }

    @Override
    @Transactional
    public SchoolPurchaseOrderResponse addItemToPurchaseOrder(
            Long orderId, SchoolPurchaseOrderItemRequest itemRequest) {
        log.info("Adding item to school purchase order ID: {}", orderId);
        
        SchoolPurchaseOrder purchaseOrder = schoolPurchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School purchase order not found with id: " + orderId));
        
        // Check if order can be modified
        if (purchaseOrder.getStatus() == SchoolOrderStatus.DELIVERED || 
            purchaseOrder.getStatus() == SchoolOrderStatus.CANCELLED) {
            throw new BusinessException("Cannot modify a " + purchaseOrder.getStatus() + " order");
        }
        
        // Check if product already exists in order
        List<SchoolPurchaseOrderItem> existingItems = schoolPurchaseOrderItemRepository
                .findByPurchaseOrderId(orderId);
        
        boolean productExists = existingItems.stream()
                .anyMatch(item -> item.getProduct().getId().equals(itemRequest.getProductId()));
        
        if (productExists) {
            throw new BusinessException("Product already exists in this purchase order. " +
                    "Consider updating the quantity instead.");
        }
        
        // Create and add new item
        SchoolPurchaseOrderItem item = createSchoolPurchaseOrderItem(itemRequest, purchaseOrder);
        schoolPurchaseOrderItemRepository.save(item);
        
        // Recalculate totals
        recalculateOrderTotals(purchaseOrder);
        
        SchoolPurchaseOrder updatedOrder = schoolPurchaseOrderRepository.save(purchaseOrder);
        log.info("Item added to school purchase order ID: {}", orderId);
        
        // Get all items for response
        List<SchoolPurchaseOrderItem> items = schoolPurchaseOrderItemRepository
                .findByPurchaseOrderId(updatedOrder.getId());
        
        return mapToResponse(updatedOrder, items);
    }

    @Override
    @Transactional
    public SchoolPurchaseOrderResponse removeItemFromPurchaseOrder(Long orderId, Long itemId) {
        log.info("Removing item ID: {} from school purchase order ID: {}", itemId, orderId);
        
        SchoolPurchaseOrder purchaseOrder = schoolPurchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School purchase order not found with id: " + orderId));
        
        // Check if order can be modified
        if (purchaseOrder.getStatus() == SchoolOrderStatus.DELIVERED || 
            purchaseOrder.getStatus() == SchoolOrderStatus.CANCELLED) {
            throw new BusinessException("Cannot modify a " + purchaseOrder.getStatus() + " order");
        }
        
        // Find and delete the item
        SchoolPurchaseOrderItem item = schoolPurchaseOrderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Item not found with id: " + itemId));
        
        if (!item.getPurchaseOrder().getId().equals(orderId)) {
            throw new BusinessException("Item does not belong to the specified order");
        }
        
        schoolPurchaseOrderItemRepository.delete(item);
        
        // Recalculate totals
        recalculateOrderTotals(purchaseOrder);
        
        SchoolPurchaseOrder updatedOrder = schoolPurchaseOrderRepository.save(purchaseOrder);
        log.info("Item removed from school purchase order ID: {}", orderId);
        
        // Get remaining items for response
        List<SchoolPurchaseOrderItem> items = schoolPurchaseOrderItemRepository
                .findByPurchaseOrderId(updatedOrder.getId());
        
        return mapToResponse(updatedOrder, items);
    }

    @Override
    @Transactional
    public SchoolPurchaseOrderResponse updateItemFinalSellingPrice(
            Long orderId, Long itemId, Double finalSellingPrice) {
        log.info("Updating final selling price for item ID: {} in order ID: {} to {}", 
                itemId, orderId, finalSellingPrice);
        
        SchoolPurchaseOrder purchaseOrder = schoolPurchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School purchase order not found with id: " + orderId));
        
        // Check if order can be modified
        if (purchaseOrder.getStatus() == SchoolOrderStatus.DELIVERED || 
            purchaseOrder.getStatus() == SchoolOrderStatus.CANCELLED) {
            throw new BusinessException("Cannot modify a " + purchaseOrder.getStatus() + " order");
        }
        
        // Find the item
        SchoolPurchaseOrderItem item = schoolPurchaseOrderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Item not found with id: " + itemId));
        
        if (!item.getPurchaseOrder().getId().equals(orderId)) {
            throw new BusinessException("Item does not belong to the specified order");
        }
        
        // Update price
        item.setFinalSellingPrice(finalSellingPrice);
        item.calculateAmounts();
        item.validateDiscount();
        
        schoolPurchaseOrderItemRepository.save(item);
        
        // Recalculate totals
        recalculateOrderTotals(purchaseOrder);
        
        SchoolPurchaseOrder updatedOrder = schoolPurchaseOrderRepository.save(purchaseOrder);
        log.info("Final selling price updated for item ID: {}", itemId);
        
        // Get all items for response
        List<SchoolPurchaseOrderItem> items = schoolPurchaseOrderItemRepository
                .findByPurchaseOrderId(updatedOrder.getId());
        
        return mapToResponse(updatedOrder, items);
    }

    @Override
    @Transactional
    public SchoolPurchaseOrderResponse updateItemDiscountPercentage(
            Long orderId, Long itemId, Double discountPercentage) {
        log.info("Updating discount percentage for item ID: {} in order ID: {} to {}", 
                itemId, orderId, discountPercentage);
        
        SchoolPurchaseOrder purchaseOrder = schoolPurchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School purchase order not found with id: " + orderId));
        
        // Check if order can be modified
        if (purchaseOrder.getStatus() == SchoolOrderStatus.DELIVERED || 
            purchaseOrder.getStatus() == SchoolOrderStatus.CANCELLED) {
            throw new BusinessException("Cannot modify a " + purchaseOrder.getStatus() + " order");
        }
        
        // Find the item
        SchoolPurchaseOrderItem item = schoolPurchaseOrderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Item not found with id: " + itemId));
        
        if (!item.getPurchaseOrder().getId().equals(orderId)) {
            throw new BusinessException("Item does not belong to the specified order");
        }
        
        // Update discount percentage
        item.setPriceWithDiscountPercentage(discountPercentage);
        item.calculateAmounts();
        item.validateDiscount();
        
        schoolPurchaseOrderItemRepository.save(item);
        
        // Recalculate totals
        recalculateOrderTotals(purchaseOrder);
        
        SchoolPurchaseOrder updatedOrder = schoolPurchaseOrderRepository.save(purchaseOrder);
        log.info("Discount percentage updated for item ID: {}", itemId);
        
        // Get all items for response
        List<SchoolPurchaseOrderItem> items = schoolPurchaseOrderItemRepository
                .findByPurchaseOrderId(updatedOrder.getId());
        
        return mapToResponse(updatedOrder, items);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SchoolPurchaseOrderSummaryResponse> getOverdueOrders() {
        log.info("Fetching overdue school purchase orders");
        
        List<SchoolPurchaseOrder> orders = schoolPurchaseOrderRepository
                .findOverdueOrders(LocalDateTime.now());
        
        return orders.stream()
                .map(order -> {
                    List<SchoolPurchaseOrderItem> items = schoolPurchaseOrderItemRepository
                            .findByPurchaseOrderId(order.getId());
                    return mapToSummaryResponse(order, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalPurchaseAmountBySchool(Long schoolId) {
        log.info("Calculating total purchase amount for school ID: {}", schoolId);
        
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School not found with id: " + schoolId);
        }
        
        Double totalAmount = schoolPurchaseOrderRepository.getTotalPurchaseAmountBySchool(schoolId);
        return totalAmount != null ? Math.round(totalAmount * 100.0) / 100.0 : 0.0;
    }

    @Override
    public DiscountCalculationResponse calculateDiscount(
            Long productId, Double finalSellingPrice, Double discountPercentage) {
        log.info("Calculating discount for product ID: {}", productId);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId));
        
        Double mrp = product.getMrp();
        
        if (finalSellingPrice != null && discountPercentage != null) {
            throw new BusinessException("Provide either final selling price OR discount percentage, not both");
        }
        
        DiscountCalculationResponse response = new DiscountCalculationResponse();
        response.setProductId(productId);
        response.setMrp(mrp);
        
        if (finalSellingPrice != null) {
            // Calculate discount percentage from final selling price
            if (finalSellingPrice > mrp) {
                throw new BusinessException("Final selling price cannot be greater than MRP");
            }
            
            Double discountAmount = mrp - finalSellingPrice;
            Double calculatedPercentage = (discountAmount / mrp) * 100;
            
            response.setFinalSellingPrice(finalSellingPrice);
            response.setDiscountPercentage(Math.round(calculatedPercentage * 100.0) / 100.0);
            response.setDiscountAmount(Math.round(discountAmount * 100.0) / 100.0);
            
        } else if (discountPercentage != null) {
            // Calculate final selling price from discount percentage
            if (discountPercentage < 0 || discountPercentage > 100) {
                throw new BusinessException("Discount percentage must be between 0 and 100");
            }
            
            Double discountAmount = mrp * (discountPercentage / 100);
            Double calculatedPrice = mrp - discountAmount;
            
            response.setFinalSellingPrice(Math.round(calculatedPrice * 100.0) / 100.0);
            response.setDiscountPercentage(discountPercentage);
            response.setDiscountAmount(Math.round(discountAmount * 100.0) / 100.0);
            
        } else {
            throw new BusinessException("Either final selling price or discount percentage must be provided");
        }
        
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public SchoolOrderStatisticsResponse getSchoolOrderStatistics(Long schoolId) {
        log.info("Fetching order statistics for school ID: {}", schoolId);
        
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School not found with id: " + schoolId));
        
        // Get all orders for the school
        List<SchoolPurchaseOrder> allOrders = schoolPurchaseOrderRepository.findBySchoolId(schoolId);
        
        // Calculate statistics
        int totalOrders = allOrders.size();
        double totalPurchaseAmount = allOrders.stream()
                .mapToDouble(SchoolPurchaseOrder::getTotalAmount)
                .sum();
        double averageOrderValue = totalOrders > 0 ? totalPurchaseAmount / totalOrders : 0;
        
        int pendingOrders = (int) allOrders.stream()
                .filter(order -> order.getStatus() == SchoolOrderStatus.PENDING)
                .count();
        int deliveredOrders = (int) allOrders.stream()
                .filter(order -> order.getStatus() == SchoolOrderStatus.DELIVERED)
                .count();
        int cancelledOrders = (int) allOrders.stream()
                .filter(order -> order.getStatus() == SchoolOrderStatus.CANCELLED)
                .count();
        
        return SchoolOrderStatisticsResponse.builder()
                .schoolId(schoolId)
                .schoolName(school.getSchoolName())
                .totalOrders(totalOrders)
                .totalPurchaseAmount(Math.round(totalPurchaseAmount * 100.0) / 100.0)
                .averageOrderValue(Math.round(averageOrderValue * 100.0) / 100.0)
                .pendingOrders(pendingOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .build();
    }

    @Override
    @Transactional
    public SchoolPurchaseOrderResponse updateItemQuantity(Long orderId, Long itemId, Integer quantity) {
        log.info("Updating quantity for item ID: {} in order ID: {} to {}", itemId, orderId, quantity);
        
        SchoolPurchaseOrder purchaseOrder = schoolPurchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "School purchase order not found with id: " + orderId));
        
        // Check if order can be modified
        if (purchaseOrder.getStatus() == SchoolOrderStatus.DELIVERED || 
            purchaseOrder.getStatus() == SchoolOrderStatus.CANCELLED) {
            throw new BusinessException("Cannot modify a " + purchaseOrder.getStatus() + " order");
        }
        
        // Find the item
        SchoolPurchaseOrderItem item = schoolPurchaseOrderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Item not found with id: " + itemId));
        
        if (!item.getPurchaseOrder().getId().equals(orderId)) {
            throw new BusinessException("Item does not belong to the specified order");
        }
        
        // Update quantity
        item.setQuantity(quantity);
        item.calculateAmounts();
        
        schoolPurchaseOrderItemRepository.save(item);
        
        // Recalculate totals
        recalculateOrderTotals(purchaseOrder);
        
        SchoolPurchaseOrder updatedOrder = schoolPurchaseOrderRepository.save(purchaseOrder);
        log.info("Quantity updated for item ID: {}", itemId);
        
        // Get all items for response
        List<SchoolPurchaseOrderItem> items = schoolPurchaseOrderItemRepository
                .findByPurchaseOrderId(updatedOrder.getId());
        
        return mapToResponse(updatedOrder, items);
    }
    
    private void recalculateOrderTotals(SchoolPurchaseOrder purchaseOrder) {
        // Get all items for this order
        List<SchoolPurchaseOrderItem> items = schoolPurchaseOrderItemRepository
                .findByPurchaseOrderId(purchaseOrder.getId());
        
        Double totalAmount = items.stream()
                .mapToDouble(SchoolPurchaseOrderItem::getTotalAmount)
                .sum();
        Double totalDiscount = items.stream()
                .mapToDouble(item -> item.getDiscountAmount() != null ? 
                        item.getDiscountAmount() * item.getQuantity() : 0.0)
                .sum();
        
        purchaseOrder.setTotalAmount(Math.round(totalAmount * 100.0) / 100.0);
        purchaseOrder.setTotalDiscount(Math.round(totalDiscount * 100.0) / 100.0);
    }



private SchoolPurchaseOrderResponse mapToResponse(
    SchoolPurchaseOrder purchaseOrder, 
    List<SchoolPurchaseOrderItem> items) {

List<SchoolPurchaseOrderItemResponse> itemResponses = items.stream()
        .map(this::mapToItemResponse)
        .collect(Collectors.toList());

return SchoolPurchaseOrderResponse.builder()
        .id(purchaseOrder.getId())
        .schoolId(purchaseOrder.getSchool().getId())
        .schoolName(purchaseOrder.getSchool().getSchoolName())
        .orderDate(purchaseOrder.getOrderDate())
        .expectedDeliveryDate(purchaseOrder.getDeliveryDate())
        .deliveryDate(purchaseOrder.getDeliveryDate())
        .status(purchaseOrder.getStatus())
        .totalAmount(purchaseOrder.getTotalAmount())
        .totalDiscount(purchaseOrder.getTotalDiscount())
        .remarks(purchaseOrder.getRemarks())
        .createdAt(purchaseOrder.getCreatedAt())
        .updatedAt(purchaseOrder.getUpdatedAt())
        .items(itemResponses)
        .build();
}

private SchoolPurchaseOrderItemResponse mapToItemResponse(SchoolPurchaseOrderItem item) {
// Calculate discount for response
item.calculateAmounts();

return SchoolPurchaseOrderItemResponse.builder()
        .id(item.getId())
        .productId(item.getProduct().getId())
        .productName(item.getProduct().getProductName())
        .productCategory(item.getProduct().getCategory())
        .brand(item.getProduct().getBrand())
        .unitOfMeasure(item.getProduct().getUnitOfMeasure())
        .quantity(item.getQuantity())
        .mrp(item.getProduct().getMrp())
        .finalSellingPrice(item.getFinalSellingPrice())
        .discountPercentage(item.getDiscountPercentage())
        .discountAmount(item.getDiscountAmount())
        .totalAmount(item.getTotalAmount())
        .build();
}

private SchoolPurchaseOrderSummaryResponse mapToSummaryResponse(
    SchoolPurchaseOrder purchaseOrder, 
    List<SchoolPurchaseOrderItem> items) {

return SchoolPurchaseOrderSummaryResponse.builder()
        .id(purchaseOrder.getId())
        .schoolId(purchaseOrder.getSchool().getId())
        .schoolName(purchaseOrder.getSchool().getSchoolName())
        .orderDate(purchaseOrder.getOrderDate())
        .expectedDeliveryDate(purchaseOrder.getDeliveryDate())
        .status(purchaseOrder.getStatus())
        .totalAmount(purchaseOrder.getTotalAmount())
        .totalDiscount(purchaseOrder.getTotalDiscount())
        .totalItems(items.size())
        .createdAt(purchaseOrder.getCreatedAt())
        .build();
}


}
