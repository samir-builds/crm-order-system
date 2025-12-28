package com.samir.crm_order_system.repository;

import com.samir.crm_order_system.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
