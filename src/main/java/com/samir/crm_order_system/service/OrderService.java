package com.samir.crm_order_system.service;

import com.samir.crm_order_system.exception.OrderNotFoundException;
import com.samir.crm_order_system.model.Order;
import com.samir.crm_order_system.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Page<Order> findAll(Pageable pageable) {
        logger.info("Sifariş siyahısı DB‑dən gətirilir...");
        Page<Order> orders =  orderRepository.findAll(pageable);
        logger.debug("DB‑dən gətirilən sifariş sayı: {}", orders.getTotalElements());
        return orders;

    }

    public Order findById(Long id) {
        logger.info("Sifariş DB‑də ID ilə axtarılır: {}", id);
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    public Order save(Order order){
        logger.info("{} üçün yeni sifariş DB‑yə yazılır", order.getCustomer().getName());
        order.setTotalPrice(order.getProduct().getPrice() *  order.getQuantity());
        Order saved = orderRepository.save(order);
        logger.info("Sifariş uğurla DB‑yə yazıldı, ID: {}", saved.getId());
        return saved;
    }
    public Order update(Long id, Order orderDetails) {
        logger.warn("Sifariş DB‑də yenilənir, ID: {}", id);
        Order order = findById(id);
        order.setCustomer(orderDetails.getCustomer());
        order.setProduct(orderDetails.getProduct());
        order.setCreatedBy(orderDetails.getCreatedBy());
        order.setQuantity(orderDetails.getQuantity());
        order.setTotalPrice(orderDetails.getProduct().getPrice() *  orderDetails.getQuantity());
        Order updated = orderRepository.save(order);
        logger.info("Sifariş uğurla yeniləndi, ID: {}", updated.getId());
        return updated;
    }

    public void delete(Long id){
        logger.warn("Sifariş DB‑dən silinir, ID: {}", id);
        findById(id);
        orderRepository.deleteById(id);
        logger.info("Sifariş uğurla silindi, ID: {}", id);
    }
}
