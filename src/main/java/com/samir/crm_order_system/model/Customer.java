package com.samir.crm_order_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "customers")
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Name can not be null")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotNull(message = "Email can not be null")
    @Email(message = "Email must be valid")
    private String email;

    @Size(max = 15, message = "Phone number must be max 15 characters")
    private String phone;
}
