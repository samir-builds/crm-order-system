package com.samir.crm_order_system.controller;

import com.samir.crm_order_system.model.Order;
import com.samir.crm_order_system.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<Order>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ){
        logger.info("Sifariş siyahısı çağırıldı: page={}, size={}, sortBy={}, direction={}", page, size, sortBy, direction);
        Pageable pageable = PageRequest.of(page, size,
                direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending()
        );
        Page<Order> orders = orderService.findAll(pageable);
        logger.debug("Tapılan sifariş sayı: {}", orders.getTotalElements());
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Order> findById(@PathVariable Long id){
        logger.info("Sifariş ID ilə axtarılır: {}", id);
        Order order = orderService.findById(id);
        if(order == null) {
            logger.error("Sifariş tapılmadı, ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.debug("Tapılan sifariş: {}", order);
        return ResponseEntity.ok(order);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Order> save(@Valid @RequestBody Order order){
        logger.info("{}: üçün yeni sifariş yaradılır", order.getCustomer().getName());
        Order saved = orderService.save(order);
        logger.info("Sifariş uğurla yaradıldı, ID: {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable Long id, @Valid @RequestBody Order order){
        logger.warn("Sifariş yenilənir, ID: {}", id);
        Order updated = orderService.update(id, order);
        logger.info("Sifariş uğurla yeniləndi, ID: {}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        logger.warn("Sifariş silinmə əməliyyatı başladı, ID: {}", id);
        orderService.delete(id);
        logger.info("Sifariş uğurla silindi, ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
