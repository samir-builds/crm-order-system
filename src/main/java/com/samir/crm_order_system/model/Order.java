package com.samir.crm_order_system.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Product product;

    @ManyToOne
    private User createdBy;

    private int quantity;
    private double totalPrice;
    private LocalDateTime orderDate = LocalDateTime.now();
}
