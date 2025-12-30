package com.samir.crm_order_system.repository;

import com.samir.crm_order_system.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
