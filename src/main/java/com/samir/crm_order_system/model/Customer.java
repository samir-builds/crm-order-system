package com.samir.crm_order_system.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="customers")
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
}
