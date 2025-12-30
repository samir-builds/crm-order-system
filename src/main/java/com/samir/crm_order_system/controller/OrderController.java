package com.samir.crm_order_system.controller;

import com.samir.crm_order_system.model.Order;
import com.samir.crm_order_system.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> findAll(){
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public Order findById(@PathVariable Long id){
        return orderService.findById(id);
    }
    @PostMapping
    public Order save(@RequestBody Order order){
        return orderService.save(order);
    }

    @PutMapping("/{id}")
    public Order update(@PathVariable Long id, @RequestBody Order order){
        return orderService.update(id, order);
    }

    @DeleteMapping
    public void delete(@PathVariable Long id){
        orderService.delete(id);
    }
}
