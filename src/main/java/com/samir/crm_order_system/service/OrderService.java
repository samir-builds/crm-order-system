package com.samir.crm_order_system.service;

import com.samir.crm_order_system.annotation.Audit;
import com.samir.crm_order_system.enums.AuditAction;
import com.samir.crm_order_system.exception.OrderNotFoundException;
import com.samir.crm_order_system.model.Customer;
import com.samir.crm_order_system.model.Order;
import com.samir.crm_order_system.model.Product;
import com.samir.crm_order_system.repository.CustomerRepository;
import com.samir.crm_order_system.repository.OrderRepository;
import com.samir.crm_order_system.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    private final EmailService emailService;

    @Value("${app.mail.admin}")
    private String adminEmail;

    public OrderService(OrderRepository orderRepository, EmailService emailService, ProductRepository productRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.emailService = emailService;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    public Page<Order> findAll(Pageable pageable) {
        logger.info("Sifariş siyahısı DB‑dən gətirilir...");
        Page<Order> orders = orderRepository.findAll(pageable);
        logger.debug("DB‑dən gətirilən sifariş sayı: {}", orders.getTotalElements());
        return orders;

    }

    public Order findById(Long id) {
        logger.info("Sifariş DB‑də ID ilə axtarılır: {}", id);
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Audit(action = AuditAction.ORDER_CREATE, entity = "Order")
    public Order save(Order order) {
        logger.info("{} üçün yeni sifariş DB‑yə yazılır", order.getCustomer().getName());

        // 🔹 Burada əlavə et
        Customer customer = customerRepository.findById(order.getCustomer().getId()).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        Product product = productRepository.findById(order.getProduct().getId()).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        order.setCustomer(customer);
        order.setProduct(product);

        if (product.getPrice() == null) {
            throw new IllegalArgumentException("Product price is null for product id: " + product.getId());
        }

        order.setTotalPrice(product.getPrice() * order.getQuantity());

        Order saved = orderRepository.save(order);
        logger.info("Sifariş uğurla DB‑yə yazıldı, ID: {}", saved.getId());

        emailService.sendSimple(adminEmail, "Yeni sifariş yaradıldı — ID: " + saved.getId(), "Order ID: " + saved.getId() + "\nCustomer: " + saved.getCustomer().getName());
        return saved;
    }


    @Audit(action = AuditAction.ORDER_UPDATE, entity = "Order")
    public Order update(Long id, Order orderDetails) {
        logger.warn("Sifariş DB‑də yenilənir, ID: {}", id);
        Order order = findById(id);
        order.setCustomer(orderDetails.getCustomer());
        order.setProduct(orderDetails.getProduct());
        order.setCreatedBy(orderDetails.getCreatedBy());
        order.setQuantity(orderDetails.getQuantity());
        order.setTotalPrice(orderDetails.getProduct().getPrice() * orderDetails.getQuantity());
        Order updated = orderRepository.save(order);
        logger.info("Sifariş uğurla yeniləndi, ID: {}", updated.getId());
        return updated;
    }

    @Audit(action = AuditAction.ORDER_DELETE, entity = "Order")
    public void delete(Long id) {
        logger.warn("Sifariş DB‑dən silinir, ID: {}", id);
        findById(id);
        orderRepository.deleteById(id);
        logger.info("Sifariş uğurla silindi, ID: {}", id);
    }
}
