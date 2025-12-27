package com.samir.crm_order_system.repository;

import com.samir.crm_order_system.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
