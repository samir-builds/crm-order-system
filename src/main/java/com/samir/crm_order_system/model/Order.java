package com.samir.crm_order_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @Min(value = 0, message = "Total price must be positive")
    private double totalPrice;

    private LocalDateTime orderDate = LocalDateTime.now();

    @Override
    public String toString() {
        return String.format(
                "Sifariş{id=%d, Müştəri ID=%d, Məhsul ID=%d, Yaradıldı=%s, Miqdar=%d, Ümumi qiymət=%.2f, Tarix=%s}",
                id,
                customer != null ? customer.getId() : null,
                product != null ? product.getId() : null,
                createdBy != null ? createdBy.getUsername() : null,
                quantity,
                totalPrice,
                orderDate
        );
    }

}
