package com.samir.crm_order_system.service;

import com.samir.crm_order_system.model.Order;
import com.samir.crm_order_system.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order Not Found with id: " + id));
    }

    public Order save(Order order){
        order.setTotalPrice(order.getProduct().getPrice() *  order.getQuantity());
        return orderRepository.save(order);
    }
    public Order update(Long id, Order orderDetails) {
        Order order = findById(id);
        order.setCustomer(orderDetails.getCustomer());
        order.setProduct(orderDetails.getProduct());
        order.setCreatedBy(orderDetails.getCreatedBy());
        order.setQuantity(orderDetails.getQuantity());
        order.setTotalPrice(orderDetails.getProduct().getPrice() *  orderDetails.getQuantity());
        return orderRepository.save(order);
    }

    public void delete(Long id){
        orderRepository.deleteById(id);
    }
}
