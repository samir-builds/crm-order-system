package com.samir.crm_order_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @NotNull(message = "Customer cannot be null")
    @ManyToOne
    private Customer customer;

    @NotNull(message = "Product cannot be null")
    @ManyToOne
    private Product product;

    @NotNull(message = "CreatedBy cannot be null")
    @ManyToOne
    private User createdBy;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @Min(value = 0, message = "Total price must be positive")
    private double totalPrice;
    private LocalDateTime orderDate = LocalDateTime.now();
}
